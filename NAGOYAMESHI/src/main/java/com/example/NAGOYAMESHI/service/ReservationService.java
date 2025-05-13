package com.example.NAGOYAMESHI.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.NAGOYAMESHI.entity.Reservation;
import com.example.NAGOYAMESHI.entity.Shop;
import com.example.NAGOYAMESHI.entity.User;
import com.example.NAGOYAMESHI.form.ReservationEditForm;
import com.example.NAGOYAMESHI.form.ReservationRegisterForm;
import com.example.NAGOYAMESHI.repository.ReservationRepository;
import com.example.NAGOYAMESHI.repository.ShopRepository;
import com.example.NAGOYAMESHI.repository.UserRepository;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ShopRepository shopRepository;
    private final UserRepository userRepository;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm"); // ★ クラス変数として定義

    public ReservationService(ReservationRepository reservationRepository, ShopRepository shopRepository,
            UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.shopRepository = shopRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void create(ReservationRegisterForm reservationRegisterForm) {
        Reservation reservation = new Reservation();
        Shop shop = shopRepository.getReferenceById(reservationRegisterForm.getShopId());
        User user = userRepository.getReferenceById(reservationRegisterForm.getUserId());
        LocalDate reservedDate = LocalDate.parse(reservationRegisterForm.getFromDateToReserve());
        LocalTime reservedTime = LocalTime.parse(reservationRegisterForm.getFromTimeToReserve());

        reservation.setShop(shop);
        reservation.setUser(user);
        reservation.setReservedDate(reservedDate);
        reservation.setReservedTime(reservedTime);
        reservation.setNumberOfPeople(reservationRegisterForm.getNumberOfPeople());

        reservationRepository.save(reservation);
    }

    public boolean isWithinCapacity(Integer numberOfPeople, Integer capacity) {
        return numberOfPeople != null && capacity != null && numberOfPeople <= capacity;
    }

    public boolean isWithinOperatingHours(String reservedDateStr, String reservedTimeStr, Shop shop) {
        if (reservedTimeStr == null) {
            return true; // 時間情報がない場合はチェックしない
        }
        try {
            LocalDate reservedDate = LocalDate.parse(reservedDateStr);
            LocalTime reservedTime = LocalTime.parse(reservedTimeStr);
            DayOfWeek dayOfWeek = reservedDate.getDayOfWeek();
            Optional<LocalTime> openingTime = getOpeningHours(shop, dayOfWeek);
            Optional<LocalTime> closingTime = getClosingHours(shop, dayOfWeek);

            if (openingTime.isPresent() && closingTime.isPresent()) {
                return !reservedTime.isBefore(openingTime.get()) && reservedTime.isBefore(closingTime.get());
            }
            return true; // 曜日の営業時間情報がない場合はチェックしない
        } catch (Exception e) {
            return false; // パースエラーの場合は営業時間外とみなす
        }
    }

    private Optional<LocalTime> getOpeningHours(Shop shop, DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY:
                return Optional.ofNullable(shop.getMondayOpeningHours()).map(time -> LocalTime.parse(time, timeFormatter));
            case TUESDAY:
                return Optional.ofNullable(shop.getTuesdayOpeningHours()).map(time -> LocalTime.parse(time, timeFormatter));
            case WEDNESDAY:
                return Optional.ofNullable(shop.getWednesdayOpeningHours()).map(time -> LocalTime.parse(time, timeFormatter));
            case THURSDAY:
                return Optional.ofNullable(shop.getThursdayOpeningHours()).map(time -> LocalTime.parse(time, timeFormatter));
            case FRIDAY:
                return Optional.ofNullable(shop.getFridayOpeningHours()).map(time -> LocalTime.parse(time, timeFormatter));
            case SATURDAY:
                return Optional.ofNullable(shop.getSaturdayOpeningHours()).map(time -> LocalTime.parse(time, timeFormatter));
            case SUNDAY:
                return Optional.ofNullable(shop.getSundayOpeningHours()).map(time -> LocalTime.parse(time, timeFormatter));
            default:
                return Optional.empty();
        }
    }

    private Optional<LocalTime> getClosingHours(Shop shop, DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY:
                return Optional.ofNullable(shop.getMondayClosingHours()).map(time -> LocalTime.parse(time, timeFormatter));
            case TUESDAY:
                return Optional.ofNullable(shop.getTuesdayClosingHours()).map(time -> LocalTime.parse(time, timeFormatter));
            case WEDNESDAY:
                return Optional.ofNullable(shop.getWednesdayClosingHours()).map(time -> LocalTime.parse(time, timeFormatter));
            case THURSDAY:
                return Optional.ofNullable(shop.getThursdayClosingHours()).map(time -> LocalTime.parse(time, timeFormatter));
            case FRIDAY:
                return Optional.ofNullable(shop.getFridayClosingHours()).map(time -> LocalTime.parse(time, timeFormatter));
            case SATURDAY:
                return Optional.ofNullable(shop.getSaturdayClosingHours()).map(time -> LocalTime.parse(time, timeFormatter));
            case SUNDAY:
                return Optional.ofNullable(shop.getSundayClosingHours()).map(time -> LocalTime.parse(time, timeFormatter));
            default:
                return Optional.empty();
        }
    }

    public void update(ReservationEditForm reservationEditForm) {
        Reservation reservation = reservationRepository.getReferenceById(reservationEditForm.getId());
        LocalDate reservedDate = LocalDate.parse(reservationEditForm.getFromDateToReserve());
        LocalTime reservedTime = LocalTime.parse(reservationEditForm.getFromTimeToReserve());

        reservation.setReservedDate(reservedDate);
        reservation.setReservedTime(reservedTime);
        reservation.setNumberOfPeople(reservationEditForm.getNumberOfPeople());

        reservationRepository.save(reservation);
    }
}