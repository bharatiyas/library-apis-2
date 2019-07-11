/*** DB Queries */
/* NOTE: 	Table names in MySql are case-sensitive */

CREATE TABLE USER (
				User_Id	INT PRIMARY KEY,
				Password VARCHAR(50) NOT NULL,
				First_Name VARCHAR(50) NOT NULL,
				Last_Name VARCHAR(50) NOT NULL,
				Date_Of_Birth DATE NOT NULL,
				Gender ENUM('Male', 'Female', 'Undisclosed') NOT NULL,
				Phone_Number VARCHAR(10) NOT NULL,
				Email_Id VARCHAR(100) NOT NULL
);

CREATE TABLE USER_SEQUENCE (
	User_Id INT NOT NULL,
  next_val INT
);

INSERT INTO USER_SEQUENCE (User_Id, next_val) VALUES (4210,4211);

/*Insert a row and then fetch it to see if it works:
		mysql> INSERT INTO USER (User_Id, Password, First_Name, Last_Name, Age, Gender, Phone_Number, Email_Id) VALUES (1, 'aBVCYR133FGW9043PMMWWfdf23', 'Robin', 'Jackman', 30, 'Male', '0412345678', 'Robin.Jackman@email.com');
		mysql> select * from USER;*/

CREATE TABLE PUBLISHER (
      Publisher_Id	INT PRIMARY KEY,
			Name VARCHAR(50) NOT NULL,
			Email_Id VARCHAR(50),
			Phone_Number VARCHAR(10)
			);

CREATE TABLE PUBLISHER_SEQUENCE (
	    Publisher_Id INT NOT NULL,
      next_val INT );

INSERT INTO PUBLISHER_SEQUENCE (Publisher_Id, next_val) VALUES (1001,1002);

CREATE TABLE BOOK (
        Book_Id	 INT PRIMARY KEY,
				ISBN VARCHAR(50) NOT NULL,
				Title VARCHAR(50) NOT NULL,
				Publisher_Id INT NOT NULL,
				Year_Published INT NOT NULL,
				Edition VARCHAR(20),
				FOREIGN KEY fk_publisher(Publisher_Id) REFERENCES PUBLISHER(Publisher_Id)
);

CREATE TABLE BOOK_SEQUENCE (
      Book_Id INT NOT NULL,
      next_val INT );

INSERT INTO BOOK_SEQUENCE (Book_Id, next_val) VALUES (2001,2002);

CREATE TABLE USER_BOOK (
    /*ID INT PRIMARY KEY,*/
    User_Id INT,
    Book_Id INT,
    Issued_Date Date NOT NULL,
    Return_Date Date NOT NULL,
    Number_Of_Times_Issued INT NOT NULL,
    FOREIGN KEY fk_user(User_Id) REFERENCES USER(User_Id),
    FOREIGN KEY fk_book(Book_Id) REFERENCES BOOK(Book_Id)
    );

/*CREATE TABLE USER_BOOK_SEQUENCE (
	  ID INT NOT NULL,
    next_val INT );

INSERT INTO USER_BOOK_SEQUENCE (ID, next_val) VALUES (1,2);*/

CREATE TABLE AUTHOR (
      Author_Id	 INT PRIMARY KEY,
			First_Name VARCHAR(50) NOT NULL,
			Last_Name VARCHAR(50) NOT NULL,
			Date_Of_Birth DATE  NOT NULL,
			Gender ENUM('Male', 'Female', 'Undisclosed') NOT NULL
			);

CREATE TABLE AUTHOR_SEQUENCE (
	    Author_Id INT NOT NULL,
      next_val INT );

INSERT INTO AUTHOR_SEQUENCE (Author_Id, next_val) VALUES (3111,3112);

CREATE TABLE BOOK_AUTHOR (
      /*ID INT PRIMARY KEY,*/
      Book_Id INT ,
      Author_Id INT,
      /*PRIMARY KEY (Book_Id, Author_Id),*/
      FOREIGN KEY fk_author(Author_Id) REFERENCES AUTHOR(Author_Id),
      FOREIGN KEY fk_book(Book_Id) REFERENCES BOOK(Book_Id)
      );

/*CREATE TABLE BOOK_AUTHOR_SEQUENCE (
	  ID INT NOT NULL,
      next_val INT );

INSERT INTO BOOK_AUTHOR_SEQUENCE (ID, next_val) VALUES (1,2);*/

CREATE TABLE BOOK_STATUS (
		/*Book_Status_Id INT NOT NULL PRIMARY KEY,*/
		Book_Id INT PRIMARY KEY,
		State ENUM('Active', 'Inactive') NOT NULL,
		Number_Of_Copies_Available INT NOT NULL,
		Number_Of_Copies_Issued INT DEFAULT 0,
		FOREIGN KEY fk_book(Book_Id) REFERENCES BOOK(Book_Id)
	);

/*CREATE TABLE BOOK_STATUS_SEQUENCE (
	  Book_Status_Id INT NOT NULL,
      next_val INT );

INSERT INTO BOOK_STATUS_SEQUENCE (Book_Status_Id, next_val) VALUES (1,2);*/