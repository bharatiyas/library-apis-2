#DB Queries#
############

## NOTE: 	Table names in MySql are case-sensitive ##

CREATE TABLE USER (
				ID	int PRIMARY KEY,
				User_Id varchar(50) NOT NULL UNIQUE,
				Password varchar(50) NOT NULL,
				First_Name varchar(50) NOT NULL,
				Last_Name varchar(50) NOT NULL,
				Date_Of_Birth date NOT NULL,
				Gender varchar(10) NOT NULL,
				Phone_Number varchar(10) NOT NULL,
				Email_Id varchar(50) NOT NULL
);

CREATE TABLE USER_SEQUENCE (
	id int NOT NULL,
  next_val int
);

INSERT INTO USER_SEQUENCE (id, next_val) VALUES (1,2);

#Insert a row and then fetch it to see if it works:
#		mysql> INSERT INTO USER (User_Id, Password, First_Name, Last_Name, Age, Gender, Phone_Number, Email_Id) VALUES (1, 'aBVCYR133FGW9043PMMWWfdf23', 'Robin', 'Jackman', 30, 'Male', '0412345678', 'Robin.Jackman@email.com');
#		mysql> select * from USER;

CREATE TABLE PUBLISHER (
			Publisher_Id int NOT NULL PRIMARY KEY,
			Name varchar(50) NOT NULL,
			Email_Id varchar(50),
			Phone_Number varchar(10)
			);

CREATE TABLE PUBLISHER_SEQUENCE (
	  Publisher_Id int NOT NULL,
      next_val int );

INSERT INTO PUBLISHER_SEQUENCE (Publisher_Id, next_val) VALUES (1,2);

CREATE TABLE BOOK (
				Book_Id int NOT NULL PRIMARY KEY,
				ISBN varchar(50) NOT NULL,
				Title varchar(50) NOT NULL,
				Publisher_Id int NOT NULL,
				Year_Published int NOT NULL,
				Edition varchar(20),
				FOREIGN KEY fk_publisher(Publisher_Id) REFERENCES PUBLISHER(Publisher_Id)
);

CREATE TABLE PUBLISHER_SEQUENCE (
	  Book_Id int NOT NULL,
      next_val int );

INSERT INTO PUBLISHER_SEQUENCE (Book_Id, next_val) VALUES (1,2);

CREATE TABLE USER_BOOK (Id int PRIMARY KEY, User_Id int, Book_Id int, Issued_Date Date NOT NULL, Return_Date Date NOT NULL, Number_Of_Times_Issued int NOT NULL, FOREIGN KEY fk_user(User_Id) REFERENCES USER(User_Id), FOREIGN KEY fk_book(Book_Id) REFERENCES BOOK(Book_Id));

CREATE TABLE AUTHOR (
			Author_Id int NOT NULL PRIMARY KEY,
			First_Name varchar(50) NOT NULL,
			Last_Name varchar(50) NOT NULL,
			Date_Of_Birth date  NOT NULL,
			Gender varchar(10) NOT NULL
			);

CREATE TABLE AUTHOR_SEQUENCE (
	  Author_Id int NOT NULL,
      next_val int );

INSERT INTO AUTHOR_SEQUENCE (Author_Id, next_val) VALUES (1,2);

CREATE TABLE BOOK_AUTHOR (Book_Id int, Author_Id int, FOREIGN KEY fk_author(Author_Id) REFERENCES AUTHOR(Author_Id), FOREIGN KEY fk_book(Book_Id) REFERENCES BOOK(Book_Id));

CREATE TABLE BOOK_AUTHOR_SEQUENCE (
	  Author_Id int NOT NULL,
      next_val int );

INSERT INTO BOOK_AUTHOR_SEQUENCE (Author_Id, next_val) VALUES (1,2);

CREATE TABLE BOOK_STATUS (
		Id int NOT NULL PRIMARY KEY,
		STATUS int NOT NULL DEFAULT 1,
		Number_Of_Copies_Available int NOT NULL,
		Number_Of_Copies_Issued int DEFAULT 0,
		FOREIGN KEY fk_book(Book_Id) REFERENCES BOOK(Book_Id)
	);

CREATE TABLE BOOK_STATUS_SEQUENCE (
	  Author_Id int NOT NULL,
      next_val int );

INSERT INTO BOOK_STATUS_SEQUENCE (Author_Id, next_val) VALUES (1,2);