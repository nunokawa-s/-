CREATE TABLE IF NOT EXISTS categories
(
   id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
   name VARCHAR (50) NOT NULL,
   created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
   updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS shops
(
   id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
   category_id INT NOT NULL,
   name VARCHAR (255) NOT NULL,
   image_name VARCHAR (255),
   description VARCHAR (255) NOT NULL,
   price INT NOT NULL,
   capacity INT NOT NULL,
   postal_code VARCHAR (8) NOT NULL,
   address VARCHAR (255) NOT NULL,
   phone_number VARCHAR (50) NOT NULL,
   monday_opening_hours VARCHAR(4) NULL,
   monday_closing_hours VARCHAR(4) NULL,
   tuesday_opening_hours VARCHAR(4) NULL,
   tuesday_closing_hours VARCHAR(4) NULL,
   wednesday_opening_hours VARCHAR(4) NULL,
   wednesday_closing_hours VARCHAR(4) NULL,
   thursday_opening_hours VARCHAR(4) NULL,
   thursday_closing_hours VARCHAR(4) NULL,
   friday_opening_hours VARCHAR(4) NULL,
   friday_closing_hours VARCHAR(4) NULL,
   saturday_opening_hours VARCHAR(4) NULL,
   saturday_closing_hours VARCHAR(4) NULL,
   sunday_opening_hours VARCHAR(4) NULL,
   sunday_closing_hours VARCHAR(4) NULL,
   created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
   updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   FOREIGN KEY (category_id) REFERENCES categories (id)
);

CREATE TABLE IF NOT EXISTS shop_category
(
   shop_id INT NOT NULL,
   category_id INT NOT NULL,
   FOREIGN KEY (shop_id) REFERENCES shops (id),
   FOREIGN KEY (category_id) REFERENCES categories (id),
   PRIMARY KEY
   (
      shop_id,
      category_id
   )
);
CREATE TABLE IF NOT EXISTS users
(
   id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
   name VARCHAR (50) NOT NULL,
   furigana VARCHAR (50) NOT NULL,
   postal_code VARCHAR (50) NOT NULL,
   address VARCHAR (255) NOT NULL,
   phone_number VARCHAR (50) NOT NULL,
   email VARCHAR (255) NOT NULL UNIQUE,
   password VARCHAR (255) NOT NULL,
   admin_flg BOOLEAN NOT NULL,
   paid_flg BOOLEAN NOT NULL,
   enabled BOOLEAN NOT NULL,
   created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
   updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS verification_tokens
(
   id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
   user_id INT NOT NULL UNIQUE,
   token VARCHAR (255) NOT NULL,
   created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
   updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS reservations
(
   id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
   shop_id INT NOT NULL,
   user_id INT NOT NULL,
   reserved_date DATE NOT NULL,
   reserved_time TIME NOT NULL,
   number_of_people INT NOT NULL,
   created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
   updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   FOREIGN KEY (shop_id) REFERENCES shops (id),
   FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS reviews
(
   id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
   shop_id INT NOT NULL,
   user_id INT NOT NULL,
   review_score INT NOT NULL,
   review_text VARCHAR (255) NOT NULL,
   created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
   updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   FOREIGN KEY (shop_id) REFERENCES shops (id),
   FOREIGN KEY (user_id) REFERENCES users (id)
);
CREATE TABLE IF NOT EXISTS nagoyameshi_db.favorites
(
   id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
   shop_id INT NOT NULL,
   user_id INT NOT NULL,
   created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
   updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   UNIQUE
   (
      shop_id,
      user_id
   ),
   FOREIGN KEY (shop_id) REFERENCES shops (id),
   FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS password_reset_token(
   id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
   user_id INT NOT NULL UNIQUE,
   token VARCHAR (255) NOT NULL,
   created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
   updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   FOREIGN KEY (user_id) REFERENCES users (id)
   );