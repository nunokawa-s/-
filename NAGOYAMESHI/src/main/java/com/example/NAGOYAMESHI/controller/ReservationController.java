package com.example.NAGOYAMESHI.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.NAGOYAMESHI.entity.Reservation;
import com.example.NAGOYAMESHI.entity.Shop;
import com.example.NAGOYAMESHI.entity.User;
import com.example.NAGOYAMESHI.form.ReservationEditForm;
import com.example.NAGOYAMESHI.form.ReservationInputForm;
import com.example.NAGOYAMESHI.form.ReservationRegisterForm;
import com.example.NAGOYAMESHI.repository.ReservationRepository;
import com.example.NAGOYAMESHI.repository.ShopRepository;
import com.example.NAGOYAMESHI.repository.UserRepository;
import com.example.NAGOYAMESHI.security.UserDetailsImpl;
import com.example.NAGOYAMESHI.service.ReservationService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ReservationController {
    private final ReservationRepository reservationRepository;
    private final ShopRepository shopRepository;
    private final ReservationService reservationService;
    private final UserRepository userRepository;


    public ReservationController(ReservationRepository reservationRepository, ShopRepository shopRepository,
            ReservationService reservationService, UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.shopRepository = shopRepository;
        this.reservationService = reservationService;
        this.userRepository = userRepository;
    }

    private boolean isWithinBusinessHours(Shop shop, LocalDate date, LocalTime time) {
        String dayOfWeek = date.getDayOfWeek().toString().toLowerCase();
        String openingHoursFieldName = dayOfWeek + "_opening_hours";
        String closingHoursFieldName = dayOfWeek + "_closing_hours";

        try {
            String openingHours = (String) Shop.class.getDeclaredField(openingHoursFieldName).get(shop);
            String closingHours = (String) Shop.class.getDeclaredField(closingHoursFieldName).get(shop);

            if (openingHours == null || closingHours == null) {
                return false; // 休業日
            }

            LocalTime openingTime = LocalTime.parse(openingHours);
            LocalTime closingTime = LocalTime.parse(closingHours);

            return !time.isBefore(openingTime) && !time.isAfter(closingTime);
        } catch (Exception e) {
            return false; // 何らかの理由でフィールドを取得できなかったら休業とみなす
        }
    }
    
    @GetMapping("/reservations")
    public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
            @PageableDefault(page = 0, size = 20, sort = "id", direction = Direction.ASC) Pageable pageable,
            Model model) {
        User user = userDetailsImpl.getUser();
        Page<Reservation> reservationPage = reservationRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        model.addAttribute("reservationPage", reservationPage);
        return "reservations/index";
    }

    @GetMapping("/shops/{id}/reservations/input")
    public String input(@PathVariable(name = "id") Integer id,
            @ModelAttribute @Validated ReservationInputForm reservationInputForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        Shop shop = shopRepository.getReferenceById(id);
        Integer numberOfPeople = reservationInputForm.getNumberOfPeople();
        Integer capacity = shop.getCapacity();
        LocalDate reservedDate = null;
        LocalTime reservedTime = null;

        if (reservationInputForm.getFromDateToReserve() != null && !reservationInputForm.getFromDateToReserve().isEmpty()) {
            try {
                reservedDate = reservationInputForm.getReservedDate();
            } catch (DateTimeParseException e) {
                FieldError fieldError = new FieldError(bindingResult.getObjectName(), "fromDateToReserve",
                        "予約日の形式が正しくありません。");
                bindingResult.addError(fieldError);
            }
        } else {
            FieldError fieldError = new FieldError(bindingResult.getObjectName(), "fromDateToReserve",
                    "予約日を選択してください。");
            bindingResult.addError(fieldError);
        }

        if (reservationInputForm.getFromTimeToReserve() != null && !reservationInputForm.getFromTimeToReserve().isEmpty()) {
            try {
                reservedTime = reservationInputForm.getReservedTime();
            } catch (DateTimeParseException e) {
                FieldError fieldError = new FieldError(bindingResult.getObjectName(), "fromTimeToReserve",
                        "予約時間の形式が正しくありません。");
                bindingResult.addError(fieldError);
            }
        } else {
            FieldError fieldError = new FieldError(bindingResult.getObjectName(), "fromTimeToReserve",
                    "予約する時間を選択してください。");
            bindingResult.addError(fieldError);
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("shop", shop);
            model.addAttribute("errorMessage", "予約内容に不備があります。");
            return "shops/show";
        }

        // その後の処理では、reservedDateとreservedTimeがnullでないことを確認するか、
        // 空の場合の処理を追加する必要があります。
        if (reservedDate != null && reservedTime != null) {
            if (!isWithinBusinessHours(shop, reservedDate, reservedTime)) {
                FieldError fieldError = new FieldError(bindingResult.getObjectName(), "fromTimeToReserve",
                        "選択された日時は営業時間外です。");
                bindingResult.addError(fieldError);
            }
        }

        if (numberOfPeople != null && !reservationService.isWithinCapacity(numberOfPeople, capacity)) {
            FieldError fieldError = new FieldError(bindingResult.getObjectName(), "numberOfPeople",
                    "人数が定員を超えています。");
            bindingResult.addError(fieldError);
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("shop", shop);
            model.addAttribute("errorMessage", "予約内容に不備があります。");
            return "shops/show";
        }

        redirectAttributes.addFlashAttribute("reservationInputForm", reservationInputForm);
        return "redirect:/shops/{id}/reservations/confirm";
    }

    @GetMapping("/shops/{id}/reservations/confirm")
    public String confirm(@PathVariable(name = "id") Integer id,
            @ModelAttribute ReservationInputForm reservationInputForm,
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
            HttpServletRequest httpServletRequest,
            Model model) {
        Shop shop = shopRepository.getReferenceById(id);
        User user = userDetailsImpl.getUser();
        String reservedDate = reservationInputForm.getFromDateToReserve();
        String reservedTime = reservationInputForm.getFromTimeToReserve();

        ReservationRegisterForm reservationRegisterForm = new ReservationRegisterForm(shop.getId(), user.getId(),
                reservedDate.toString(), reservedTime.toString(), reservationInputForm.getNumberOfPeople());

        model.addAttribute("shop", shop);
        model.addAttribute("reservationRegisterForm", reservationRegisterForm);
        return "reservations/confirm";
    }

    @PostMapping("/shops/{shopId}/reservations/create")
    public String create(@PathVariable("shopId") Integer shopId,
            @Validated @ModelAttribute ReservationRegisterForm reservationRegisterForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        Shop shop = shopRepository.getReferenceById(shopId);

        if (bindingResult.hasErrors()) {
            model.addAttribute("shop", shop);
            model.addAttribute("reservationRegisterForm", reservationRegisterForm);
            return "reservations/confirm";
        }

        if (!reservationService.isWithinOperatingHours(reservationRegisterForm.getFromDateToReserve(), reservationRegisterForm.getFromTimeToReserve(), shop)) {
            model.addAttribute("shop", shop);
            model.addAttribute("reservationRegisterForm", reservationRegisterForm);
            model.addAttribute("errorMessage", "この時間帯は営業時間外です。");
            return "reservations/confirm";
        }

        if (!reservationService.isWithinCapacity(reservationRegisterForm.getNumberOfPeople(), shop.getCapacity())) {
            model.addAttribute("shop", shop);
            model.addAttribute("reservationRegisterForm", reservationRegisterForm);
            model.addAttribute("errorMessage", "予約人数が定員数を超えています。");
            return "reservations/confirm";
        }

        try {
            reservationService.create(reservationRegisterForm);
            redirectAttributes.addFlashAttribute("successMessage", "予約が完了しました。");
            return "redirect:/shops/" + shopId;
        } catch (Exception e) {
            model.addAttribute("shop", shop);
            model.addAttribute("errorMessage", "予約処理中にエラーが発生しました。");
            return "reservations/confirm";
        }
    }

	// 予約完了を表示するメソッド
	@GetMapping("/shops/{id}/reservations/index")
	public String complete(@PathVariable("id") Integer id, Model model) {
		Shop shop = shopRepository.getReferenceById(id);
		model.addAttribute("shop", shop);
		return "reservations/index";
	}

	@GetMapping("/shops/{shopId}/edit/{userId}/{reservationId}")
	public String edit(@PathVariable(name = "shopId") Integer shopId, @PathVariable(name = "userId") Integer userId,
			@PathVariable(name = "reservationId") Integer reservationId, Model model) {
		Shop shop = shopRepository.getReferenceById(shopId);
		User user = userRepository.getReferenceById(userId);
		Reservation reservation = reservationRepository.getReferenceById(reservationId);

		ReservationEditForm reservationEditForm = new ReservationEditForm(
				reservation.getShop().getId(), // Shop の ID を取得
				reservation.getUser().getId(), // User の ID を取得
				reservation.getReservedDate().toString(),
				reservation.getReservedTime().toString(),
				reservation.getNumberOfPeople());

	    reservationEditForm.setUserId(userId); 
	    reservationEditForm.setId(reservation.getId()); // 追加


		model.addAttribute("shop", shop);
		model.addAttribute("user", user);
		model.addAttribute("reservation", reservation);
		model.addAttribute("reservationEditForm", reservationEditForm);

		return "reservations/edit";
	}

	@PostMapping("/shops/{shopId}/update/{userId}/{reservationId}")
	@Transactional
	public String update(@PathVariable(name = "shopId") Integer shopId, @PathVariable(name = "userId") Integer userId,
			@PathVariable(name = "reservationId") Integer reservationId,
			@ModelAttribute @Validated ReservationEditForm reservationEditForm, BindingResult bindingResult,
			RedirectAttributes redirectAttributes, Model model) {
		Shop shop = shopRepository.getReferenceById(shopId);
		User user = userRepository.getReferenceById(userId);
		Reservation reservation = reservationRepository.getReferenceById(reservationId);

		if (bindingResult.hasErrors()) {
			model.addAttribute("shop", shop);
			model.addAttribute("user", user);
			model.addAttribute("reservation", reservation);
			model.addAttribute("reservationRegisterForm", reservationEditForm); // フォームを再表示するために必要

			return "reviews/edit";
		}

		reservationService.update(reservationEditForm);
		redirectAttributes.addFlashAttribute("successMessage", "予約を編集しました。");

		return "redirect:/reservations";
	}

	@PostMapping("/reservations/{reservationId}/delete")
	public String delete(@PathVariable(name = "reservationId") Integer reservationId,
			RedirectAttributes redirectAttributes) {

		reservationRepository.deleteById(reservationId);

		redirectAttributes.addFlashAttribute("successMessage", "予約を削除しました。");

		return "redirect:/reservations";
	}
}
