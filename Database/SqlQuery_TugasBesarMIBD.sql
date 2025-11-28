DROP TABLE IF EXISTS Takedown
DROP TABLE IF EXISTS Publish
DROP TABLE IF EXISTS Edit
DROP TABLE IF EXISTS Subscribe
DROP TABLE IF EXISTS Komen
DROP TABLE IF EXISTS Menonton
DROP TABLE IF EXISTS Dislike
DROP TABLE IF EXISTS Likes
DROP TABLE IF EXISTS Kanal_Individu
DROP TABLE IF EXISTS Kanal_Group
DROP TABLE IF EXISTS Konten
DROP TABLE IF EXISTS Undangan
DROP TABLE IF EXISTS Pengguna
DROP TABLE IF EXISTS Kanal

--select * from Kanal
--select * from Pengguna
--select * from Konten
--select * from Kanal_Group
--select * from Kanal_Individu
--select * from Likes
--select * from Dislike
--select * from Menonton
--select * from Komen
--select * from Subscribe
--select * from Edit
--select * from Publish

CREATE TABLE Kanal (
	idKanal int NOT NULL PRIMARY KEY,
	namaKanal varchar(75),
	deskripsiKanal varchar(150),
	tanggal_PembuatanKanal date,
	website varchar(250)
)

INSERT INTO Kanal
VALUES ('1', 'Dodo Channel', 'Kami suka yuzu', '2008-07-13','www.criptoAcademy.edu'),
('2', 'Wombat Channel', 'Aku suka membuat robot', '2012-05-17','www.youtube.com'),
('3', 'Axel Constantijn Gaming', 'Aku suka yuzu', '2008-07-20','www.studentportal.unpar.ac.id'),
('4', 'Cooking With Ica', 'belajar memasak dengan Little Ica', '2010-10-12','www.ide.unpar.ac.id'),
('5', 'Making Money Academy', 'belajar cara menghasilkan uang dengan cepat dan efisien', '2020-01-01','www.labftis.unpar.ac.id')

CREATE TABLE Pengguna(
	idPengguna int NOT NULL PRIMARY KEY,
	namaP varchar(75),
	password_Pengguna varchar(75),
	email varchar(75),
	tanggal_Buat date,
	tipe_Pengguna int,
	jabatan varchar(50),
	tanggal_Undang date,
	idKanal int FOREIGN KEY REFERENCES Kanal (idKanal)
)


INSERT INTO Pengguna
VALUES ('1', 'Dodo Bird', 'dodoganteng123', 'dodo123@gmail.com', '2008-06-01', '1', 'Editor', '2008-07-20', null),
('2', 'Wombat', 'wombat123', 'wombat123@gmail.com', '2012-03-01', '1', null, null, '2'),
('3', 'Axel Constantijn', 'sangatokdabes123', 'constance321@gmail.com', '2008-06-05', '1', null, null, '3'),
('4', 'Little Ica', 'fatf123', 'littlegirl@gmail.com', '2009-01-21', '1', null, null, '4'),
('5', 'Anaxagoras', 'abckuadrat', 'phytagoras@gmail.com', '2008-07-07', '0', null, null, null),
('6', 'Timothy Ronald', 'kripto123', 'timothy80@gmail.com', '2019-07-07', '1', 'Owner', null, '5'),
('7', 'Raja Kripto', 'akubocilkripto', 'dewakripto17@gmail.com', '2020-03-02', '1', 'Viewer', '2020-04-30', null)

CREATE TABLE Konten(
	idKonten int NOT NULL PRIMARY KEY,
	judul varchar(75),
	deskripsiVideo varchar(75),
	durasiVideo time,
	status_Penghapusan int,
	pathVideo varchar(225),
	idKanal int FOREIGN KEY REFERENCES Kanal (idKanal)
)


INSERT INTO Konten
VALUES (1, 'Yuzu Pertama', 'Review minuman yuzu', '00:10:25', '0', 'C:\Users\ASUS\Downloads\Video1', '1'),
(2, 'Robot Pintar', 'Demonstrasi robot buatan sendiri', '00:15:40', '0', 'C:\Users\ASUS\Downloads\Video2','2'),
(3, 'Game Seru', 'Main bareng di game favorit', '00:20:05', '0', 'C:\Users\ASUS\Downloads\Video3','3'),
(4, 'Resep Nasi Goreng Spesial', 'Masak cepat ala Ica', '00:08:30', '0', 'C:\Users\ASUS\Downloads\Video4','4'),
(5, 'Cara Dapat Uang dari Internet', 'Strategi monetisasi cepat', '00:12:45', '0', 'C:\Users\ASUS\Downloads\Video5','5'),
(6, 'Unboxing Robot Baru', 'Review dan uji coba robot', '00:10:15', '0', 'C:\Users\ASUS\Downloads\Video6','2'),
(7, 'Yuzu Mix Challenge', 'Eksperimen minuman yuzu', '00:07:20', '0', 'C:\Users\ASUS\Downloads\Video1','4'),
(8, 'Failed Cooking Attempt', 'Eksperimen gagal membuat kue', '00:05:30', '1', 'C:\Users\ASUS\Downloads\Video8', '4'),
(9, 'Scam Alert!', 'Video diblokir karena informasi menyesatkan', '00:09:10', '1', 'C:\Users\ASUS\Downloads\Video9', '5');

UPDATE Konten
SET pathVideo = pathVideo+'.mp4'

CREATE TABLE Kanal_Group(
	idKanal int FOREIGN KEY REFERENCES Kanal (idKanal),
	jumlah_Anggota int,
	namaBrand varchar(50),
	passBrand varchar(75),
	emailBrand varchar(75)
)

INSERT INTO Kanal_Group
VALUES ('1', '2', 'Dodo Brand', 'dodokeren123', 'dodoBrand@gmail.com'),
('5', '2', 'Kripto Academy', 'kopi80ribu', 'timothy20@gmail.com')


CREATE TABLE Kanal_Individu(
	idKanal int FOREIGN KEY REFERENCES Kanal (idKanal)
)

INSERT INTO Kanal_Individu
VALUES ('2'),
('3'),
('4')


CREATE TABLE Likes (
	idKonten INT,
    idPengguna INT,
    PRIMARY KEY (idKonten, idPengguna),
    FOREIGN KEY (idKonten) REFERENCES Konten (idKonten),
    FOREIGN KEY (idPengguna) REFERENCES Pengguna (idPengguna),
	tanggalLike date,
	status_Penghapusan int
)


INSERT INTO Likes
VALUES	(1, 2, '2023-08-01', '0'),  -- Wombat menyukai video "Yuzu Pertama"
(3, 4, '2023-09-12', '0'),  -- Little Ica menyukai video "Game Seru"
(5, 7, '2024-01-15', '0'),  -- Raja Kripto menyukai video "Cara Dapat Uang dari Internet"
(4, 1, '2023-10-20', '0');  -- Dodo Bird menyukai video "Resep Nasi Goreng Spesial"




CREATE TABLE Dislike(
	idKonten INT,
    idPengguna INT,
    PRIMARY KEY (idKonten, idPengguna),
    FOREIGN KEY (idKonten) REFERENCES Konten (idKonten),
    FOREIGN KEY (idPengguna) REFERENCES Pengguna (idPengguna),
	tanggalDislike date,
	status_Penghapusan int
)

INSERT INTO Dislike
VALUES 
(2, 5, '2023-08-10', '0'),  -- Anaxagoras tidak suka video "Robot Pintar"
(6, 1, '2023-09-15', '0'),  -- Dodo Bird tidak suka video "Unboxing Robot Baru"
(3, 7, '2024-02-01', '0'),  -- Raja Kripto tidak suka video "Game Seru"
(5, 3, '2024-03-05', '0');  -- Axel Constantijn tidak suka video "Cara Dapat Uang dari Internet"

CREATE TABLE Menonton(
	idKonten INT,
    idPengguna INT,
    PRIMARY KEY (idKonten, idPengguna),
    FOREIGN KEY (idKonten) REFERENCES Konten (idKonten),
    FOREIGN KEY (idPengguna) REFERENCES Pengguna (idPengguna),
	tanggalMenonton date,
	durasiMenonton time
)


INSERT INTO Menonton
VALUES 
(1, 2, '2024-04-01', '00:10:00'),  -- Wombat menonton hampir penuh video "Yuzu Pertama"
(3, 4, '2024-04-03', '00:15:00'),  -- Little Ica menonton sebagian video "Game Seru"
(5, 7, '2024-04-05', '00:12:45');  -- Raja Kripto menonton penuh video "Cara Dapat Uang dari Internet"


CREATE TABLE Komen(
	idKonten INT,
    idPengguna INT,
    PRIMARY KEY (idKonten, idPengguna),
    FOREIGN KEY (idKonten) REFERENCES Konten (idKonten),
    FOREIGN KEY (idPengguna) REFERENCES Pengguna (idPengguna),
	tanggalKomen date,
	isiKomen varchar(150),
	status_Penghapusan int
)

INSERT INTO Komen
VALUES 
(1, 2, '2024-04-10', 'Video ini bikin aku pengen minum yuzu terus!', '0'),
(3, 4, '2024-04-11', 'Seru banget main gamenya, aku jadi pengen coba juga.', '0'),
(5, 7, '2024-04-12', 'Mantap! Cara dapet uangnya jelas dan gampang dipahami.', '0'),
(2, 5, '2024-04-13', 'Robotnya keren, tapi penjelasannya agak cepat.', '0');


CREATE TABLE Subscribe(
	idKanal int,
	idPengguna int,
	PRIMARY KEY (idKanal, idPengguna),
	FOREIGN KEY (idKanal) REFERENCES Kanal (idKanal),
	FOREIGN KEY (idPengguna) REFERENCES Pengguna(idPengguna),
	tanggalSubscribe date,
	tanggalUnsubscribe date,
	status_Penghapusan int
)

INSERT INTO Subscribe
VALUES
(1, 2, '2023-07-10', NULL, 0),  -- Wombat subscribe ke Dodo Channel
(3, 4, '2023-08-05', NULL, 0),  -- Little Ica subscribe ke Axel Constantijn Gaming
(5, 2, '2024-01-12', NULL, 0),  -- wombat subscribe ke king cripto Channel
(2, 1, '2023-09-01', '2024-02-15', 1);  -- Dodo Bird subscribe ke Wombat Channel tapi sudah unsubscribed (status_Penghapusan = 1)

insert into Subscribe
values (3,1,'2023-09-10',null,0) 
insert into Subscribe
values(4,1,'2024-01-01',null,0)



CREATE TABLE Edit (
	idKonten int,
	idPengguna int,
	PRIMARY KEY (idKonten, idPengguna),
	FOREIGN KEY (idKonten) REFERENCES Konten(idKonten),
	FOREIGN KEY (idPengguna) REFERENCES Pengguna(idPengguna),
	tanggal_Edit date
)

INSERT INTO Edit
VALUES 
(1, 1, '2024-03-01'),  -- Dodo Bird mengedit video "Yuzu Pertama"
(2, 2, '2024-03-05'),  -- Wombat mengedit video "Robot Pintar"
(4, 4, '2024-03-10'),  -- Little Ica mengedit video "Resep Nasi Goreng Spesial"
(5, 6, '2024-03-15');  -- Timothy Ronald mengedit video "Cara Dapat Uang dari Internet"


CREATE TABLE Publish(
	idKonten int,
	idPengguna int,
	PRIMARY KEY (idKonten, idPengguna),
	FOREIGN KEY (idKonten) REFERENCES Konten(idKonten),
	FOREIGN KEY (idPengguna) REFERENCES Pengguna(idPengguna),
	tanggal_Publish date
)


INSERT INTO Publish
VALUES 
(1, 1, '2008-07-13'),  -- Dodo Bird mem-publish "Yuzu Pertama"
(2, 2, '2012-05-17'),  -- Wombat mem-publish "Robot Pintar"
(4, 4, '2010-10-12'),  -- Little Ica mem-publish "Resep Nasi Goreng Spesial"
(5, 6, '2020-01-01');  -- Timothy Ronald mem-publish "Cara Dapat Uang dari Internet"



CREATE TABLE Takedown(
	idKonten int,
	idPengguna int,
	PRIMARY KEY (idKonten, idPengguna),
	FOREIGN KEY (idKonten) REFERENCES Konten(idKonten),
	FOREIGN KEY (idPengguna) REFERENCES Pengguna(idPengguna),
	tanggal_Takedown date
)

INSERT INTO Takedown
VALUES 
(8, 4, '2024-04-20'),  -- Little Ica menurunkan video gagal masak
(9, 6, '2024-04-22');  -- Timothy Ronald menurunkan video scam/informasi menyesatkan