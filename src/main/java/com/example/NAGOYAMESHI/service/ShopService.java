package com.example.NAGOYAMESHI.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.NAGOYAMESHI.entity.Shop;
import com.example.NAGOYAMESHI.form.ShopRegisterForm;
import com.example.NAGOYAMESHI.repository.ShopRepository;

@Service
public class ShopService {
	private final ShopRepository shopRepository;
	
	public ShopService(ShopRepository shopRepository) {
		this.shopRepository = shopRepository;
	}
	
	@Transactional
	public void create(ShopRegisterForm shopRegisterForm ) {
		Shop shop = new Shop();
		MultipartFile imageFile = shopRegisterForm.getImageFile();
		
	if (!imageFile.isEmpty()) {
		String imageName = imageFile.getOriginalFilename();
		String hashdImageName = generateNewFileName(imageName);
		Path filePath = Paths.get("\"src/main/resources/static/storage/" + hashdImageName);
		copyImageFile(imageFile, filePath);
		shop.setImageName(hashdImageName);
	}
	 shop.setName(shopRegisterForm.getName());                
     shop.setDescription(shopRegisterForm.getDescription());
     shop.setPrice(shopRegisterForm.getPrice());
     shop.setCapacity(shopRegisterForm.getCapacity());
     shop.setPostalCode(shopRegisterForm.getPostalCode());
     shop.setAddress(shopRegisterForm.getAddress());
     shop.setPhoneNumber(shopRegisterForm.getPhoneNumber());
                 
     shopRepository.save(shop);
	}
	 // UUIDを使って生成したファイル名を返す
	   public String generateNewFileName(String fileName) {
	       String[] fileNames = fileName.split("\\.");                
	       for (int i = 0; i < fileNames.length - 1; i++) {
	           fileNames[i] = UUID.randomUUID().toString();            
	       }
	       String hashedFileName = String.join(".", fileNames);
	       return hashedFileName;
	   }     
	   
	   // 画像ファイルを指定したファイルにコピーする
	   public void copyImageFile(MultipartFile imageFile, Path filePath) {           
	       try {
	           Files.copy(imageFile.getInputStream(), filePath);
	       } catch (IOException e) {
	           e.printStackTrace();
	       }          
	   } 
}
