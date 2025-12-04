import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class YoutubeUI {
    private static int maxIdPengguna;
    private static int maxIdKanal;
    private static int maxIdKonten;
    private static String currentUserEmail;
    private static String currentUsername;
    private static String currentUserId;
    private static int currentUserType = -1; // -1 = not signed in, 0 = group, 1 = individu, 2 = sign in
    private static int currentUserRole; // 0 = owner, 1 = manager, 2 = editor, 3 = editor L, 4 = subtitle editor, 5 =
                                        // viewer

    public static Scanner sc = new Scanner(System.in);

    public static ConnectionDB connection = new ConnectionDB();

    public static void main(String[] args) throws SQLException {
        String welcome = String.format(
                " __        __   _                            \n" +
                        " \\ \\      / /__| | ___ ___  _ __ ___   ___  \n" +
                        "  \\ \\ /\\ / / _ \\ |/ __/ _ \\| '_ ` _ \\ / _ \\ \n" +
                        "   \\ V  V /  __/ | (_| (_) | | | | | |  __/ \n" +
                        "    \\_/\\_/ \\___|_|\\___\\___/|_| |_| |_|\\___|  ");

        System.out.println(welcome);
        connection.setQuery("select MAX(idPengguna) from Pengguna");
        maxIdPengguna = Integer.parseInt(connection.exeQuery(1, 1).trim()) + 1;

        connection.setQuery("select MAX(idKanal) from Kanal");
        maxIdKanal = Integer.parseInt(connection.exeQuery(1, 1).trim()) + 1;

        connection.setQuery("select MAX(idKonten) from Konten");
        maxIdKonten = Integer.parseInt(connection.exeQuery(1, 1).trim()) + 1;
        menuNotSignedIn();

    }

    public static void menuNotSignedIn() {
        currentUserType = -1;
        System.out.println("\n\n----------Menu----------");
        System.out.println("[1] : Sign In");
        System.out.println("[2] : Register");
        System.out.println("[3] : Watch Video");
        System.out.println("------------------------");
        System.out.print("Please Select Menu: ");
        int pilihan = sc.nextInt();
        switch (pilihan) {
            case 1:
                signIn();
                break;
            case 2:
                register();
                break;
            case 3:
                watchnotSignin();
                break;
        }
    }

    /*
     * GROUP
     * NOT SIGN IN
     */
    public static void signIn() {
        System.out.println("\n\n-------------Sign In----------------");
        System.out.println("Please enter following information:");
        System.out.print("Email Address : ");
        String email = sc.next().trim();
        System.out.print("Password      : ");
        String password = sc.next().trim();

        connection.setQuery("Select password_Pengguna from Pengguna where email = '" + email + "'");
        String validPass = connection.exeQuery(1, 1).trim();
        if (validPass == "" || !validPass.equals(password)) {
            System.out.println("-----------------------------------------------------");
            System.out.println("Email or Password did not match / User not Registered.");
            System.out.println("                 Please try again.");
            System.out.println("-----------------------------------------------------");
            signIn();
        } else {
            currentUserEmail = email;
            connection.setQuery("Select namaP from Pengguna where email = '" + email + "'");
            currentUsername = connection.exeQuery(1, 1).trim();
            connection.setQuery("Select idPengguna from Pengguna where email = '" + email + "'");
            currentUserId = connection.exeQuery(1, 1).trim();
            connection.setQuery("select jabatan from Pengguna where idPengguna = '" + currentUserId + "'");
            String tempRole = connection.exeQuery(1, 1).trim();

            if (tempRole.equals("Owner"))
                currentUserRole = 0;
            else if (tempRole.equals("Manager"))
                currentUserRole = 1;
            else if (tempRole.equals("Editor"))
                currentUserRole = 2;
            else if (tempRole.equals("Editor (Limited)"))
                currentUserRole = 3;
            else if (tempRole.equals("Subtitle Editor"))
                currentUserRole = 4;
            else
                currentUserRole = 5;

            connection.setQuery("select idKanal from Pengguna where idPengguna = '" + currentUserId + "'");
            String temp = connection.exeQuery(1, 1).trim();
            if (!temp.equalsIgnoreCase("NULL")) {
                connection.setQuery("select KanalGroup.idKanal from (select idKanal from Pengguna where idPengguna = '"
                        + currentUserId
                        + "'  ) as iddkanal join KanalGroup on iddkanal.idKanal = KanalGroup.idKanal");
                String temp1 = connection.exeQuery(1, 1).trim();

                if (temp1.equals("")) {
                    currentUserType = 1;
                    menuAfterChannelCreatedIndividu();
                } else {
                    currentUserType = 0;
                    menuAfterChannelCreatedGroup();
                }
            } else {
                currentUserType = 2;
                menuAfterSignIn();
            }

        }
    }

    public static void register() {
        System.out.println("\n\n-------------Register----------------");
        System.out.println("Please enter following information:");
        System.out.print("Username          : ");
        String username = sc.next();
        System.out.print("Email Address     : ");
        String email = sc.next();
        System.out.print("Password          : ");
        String password = sc.next();
        System.out.print("Confirm password  : ");
        String confirmPass = sc.next();

        while (!confirmPass.equals(password)) {
            System.out.println("-----------------------------------------------------");
            System.out.println(" Password mismatch, please enter your password again");
            System.out.println("-----------------------------------------------------");

            System.out.printf("Username          : %s\n", username);
            System.out.printf("Email Address     : %s\n", email);
            System.out.print("Password          : ");
            password = sc.next();
            System.out.print("Confirm password  : ");
            confirmPass = sc.next();
        }
        if (password.equals(confirmPass)) {
            // idPengguna int NOT NULL PRIMARY KEY,
            // namaP varchar(75),
            // password_Pengguna varchar(75),
            // email varchar(75),
            // tanggal_Buat date,
            // tipe_Pengguna int,
            // jabatan varchar(50),
            // tanggal_Undang date,
            // idKanal int FOREIGN KEY REFERENCES Kanal (idKanal)
            connection.setQuery("insert into Pengguna values ('" + maxIdPengguna++ + "', '" + username + "', '"
                    + password + "', '" + email + "', '" + LocalDate.now() + "', '" + 0 + "', " + "null, " + "null, "
                    + "null)");
            connection.exeInsertQuery();
            signIn();
        }
    }

    public static void watchnotSignin() {
        int id = 0;
        connection.setQuery("Select MAX(idKonten) from Konten");
        int max = Integer.parseInt(connection.exeQuery(1, 1).trim());
        while (true) {
            connection.setQuery(
                    "Select idKonten, judul, durasiVideo, namaKanal from Konten JOIN Kanal ON Konten.idKanal = Kanal.idKanal where idKonten > "
                            + id
                            + "AND status_Penghapusan = 0");
            System.out.println("\n\nNo-----Title--------------Duration---------------Channel----------");
            connection.printQuery(4, 3);
            System.out.println("------------------------------------------------------------------");
            System.out.println("[1] = Previous Page");
            System.out.println("[2] = Next Page");
            System.out.println("[3] = Choose Video");
            System.out.print("Please Select Menu: ");
            int choice = sc.nextInt();
            if (choice == 1) {
                if (id > 0) {
                    id -= 3;
                } else {
                    System.out.println("Already at the first page.");
                }
            } else if (choice == 2) {
                if (id < max) {
                    id += 3;
                } else {
                    System.out.println("Already at the last page.");
                }
            } else if (choice == 3) {
                break;
            }
        }

        System.out.println("Which one do you want to watch?");
        System.out.print("Video Number: ");
        int pilihan = sc.nextInt();
        videonotSignIn(pilihan);
    }

    public static void watchNotSigninOneChannel(String idKanal) {
        int page = 0;
        connection.setQuery(
                "SELECT COUNT(idKonten) FROM Konten WHERE idKanal = " + idKanal + " AND status_Penghapusan = 0");
        int total = Integer.parseInt(connection.exeQuery(1, 1).trim());
        int maxPage = (int) Math.ceil(total / 3.0);

        while (true) {
            int offset = page * 3;
            int startRow = offset + 1;
            int endRow = startRow + 3 - 1;
            connection.setQuery(
                    "SELECT idKonten, judul, durasiVideo FROM ("
                            + "SELECT idKonten, judul, durasiVideo, "
                            + "ROW_NUMBER() OVER (ORDER BY idKonten) AS row_num "
                            + "FROM Konten WHERE idKanal = " + idKanal + " AND status_Penghapusan = 0"
                            + ") AS numbered "
                            + "WHERE row_num BETWEEN " + startRow + " AND " + endRow);

            System.out.println("\n\nNo----Title-------------Duration----");
            connection.printQuery(3, 3);
            System.out.println("------------------------------------");
            System.out.println("[1] = Previous Page");
            System.out.println("[2] = Next Page");
            System.out.println("[3] = Choose Video");
            System.out.print("Please Select Menu: ");
            int choice = sc.nextInt();
            if (choice == 1) {
                if (page > 0) {
                    page--;
                } else {
                    System.out.println("Already at the first page.");
                }
            } else if (choice == 2) {
                if (page < maxPage - 1) {
                    page++;
                } else {
                    System.out.println("Already at the last page.");
                }
            } else if (choice == 3) {
                break;
            }
        }

        System.out.println("Which one do you want to watch?");
        System.out.print("Video Number: ");
        int pilihan = sc.nextInt();
        videonotSignIn(pilihan);
    }

    public static void videonotSignIn(int choice) {
        connection.setQuery("select pathVideo from Konten where idKonten = " + choice);
        String path = connection.exeQuery(1, 1).trim();
        File videoFile = new File(path);

        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(videoFile);
            } catch (IOException e) {
                System.out.println("Video unreachable.");
                videonotSignIn(choice);
            }
        }

        connection.setQuery("select judul from Konten where idKonten = '" + choice + "'");
        String judul = connection.exeQuery(1, 1).trim();
        while (true) {
            System.out.println("\n\n---------------------------------");
            System.out.printf("Now Playing: %s...\n", judul);
            System.out.println("---------------------------------");

            System.out.println("[1]: View Channel");
            System.out.println("[2]: View All Comment");
            System.out.println("[3]: Back to Home Page");
            System.out.println("---------------------------------");

            System.out.print("Enter your menu choice: ");
            int pilihan = sc.nextInt();

            if (pilihan == 1) {
                viewChannelNotSignedIn(choice);
                break;
            } else if (pilihan == 3) {
                menuNotSignedIn();
                break;
            } else {
                System.out.println("Invalid choice.");
                videonotSignIn(choice);
            }
        }
    }

    public static void viewChannelNotSignedIn(int choice) {
        connection.setQuery("select TOP 1 idKanal from Konten where idKonten = " + choice);
        String idKanal = connection.exeQuery(1, 1).trim();

        connection.setQuery("select namaKanal from Kanal where idKanal = " + idKanal);
        String namaKanal = connection.exeQuery(1, 1).trim();
        System.out.printf("\n\nChannel Name: %s\n", namaKanal);
        System.out.println("[1]: View Video List");
        System.out.println("[2]: View Channel Description");
        System.out.println("[3]: Go to This Channel Website");
        System.out.println("[4]: Go to Home Page");
        System.out.println("--------------------------------");
        System.out.print("Please select menu: ");
        int pilihan = sc.nextInt();
        switch (pilihan) {
            case 1:
                watchNotSigninOneChannel(idKanal);
                break;
            case 2:
                viewChannelDescription(idKanal, namaKanal, choice);
                break;
            case 3:
                connection.setQuery("select website from Kanal where idKanal = " + idKanal);
                String website = connection.exeQuery(1, 1).trim();
                if (Desktop.isDesktopSupported()) {
                    try {
                        URI link = new URI(website);
                        Desktop.getDesktop().browse(link);
                    } catch (IOException e) {
                        System.out.println("Video unreachable.");
                        videonotSignIn(choice);
                    } catch (URISyntaxException e) {
                        System.out.println("Website is unreachable.");
                        videonotSignIn(choice);
                    }
                }
                viewChannelNotSignedIn(choice);
                break;
            case 4:
                menuNotSignedIn();
                break;
            default:
                System.out.println("Invalid choice.");
                viewChannelNotSignedIn(choice);
                break;
        }
    }

    public static void viewChannelDescription(String idKanal, String namaKanal, int choice) {
        connection.setQuery("select deskripsiKanal from Kanal where idKanal = " + idKanal);
        String description = connection.exeQuery(1, 1).trim();

        System.out.println("\n");
        System.out.println(namaKanal + " Description :");
        System.out.println(description + "\n");

        System.out.print("Press [1] to go back: ");
        int goBack = 0;
        while (goBack != 1) {
            goBack = sc.nextInt();
            if (goBack == 1) {
                if (currentUserType == -1)
                    viewChannelNotSignedIn(choice);
                else
                    viewChannelSignedIn(choice);
                break;
            } else if (goBack != 0) {
                System.out.println("Invalid choice.");
            }
        }
    }

    /*
     * GROUP
     * SIGN IN
     */
    public static void menuAfterSignIn() {
        System.out.println("\n\n----------Welcome, " + currentUsername + "!----------");
        System.out.println("[1] : Create Channel");
        System.out.println("[2] : Watch Video");
        System.out.println("[3] : View Subcribed Channel");
        System.out.println("[4] : Sign Out");
        System.out.println("--------------------------------------");
        System.out.print("Please Select Menu: ");
        int pilihan = sc.nextInt();

        switch (pilihan) {
            case 1:
                System.out.println("[1] : Individual");
                System.out.println("[2] : Group");
                System.out.print("What type of channel do you want to create?: ");
                if (sc.nextInt() == 1) {
                    // klo individu, update tabel individu, atribut di individu cuman FK idKanal
                    createChannel();
                } else {
                    // klo group, update tabel group, atribut di group : FK idKanal sama
                    // jumlah_Anggota
                    createGroupChannel();
                }
                break;
            case 2:
                watchSignin(2);
                break;
            case 3:
                viewSubscribedChannel();
                break;
            case 4:
                currentUserType = -1;
                menuNotSignedIn();
                break;
        }
    }

    public static void viewSubscribedChannel() {
        connection.setQuery("select Kanal.namaKanal from (select idKanal from Subscribe where idPengguna = '"
                + currentUserId + "') as kanalSubs JOIN Kanal ON kanalSubs.idKanal = Kanal.idKanal");
        System.out.println("\n\nAll Subscribed Channel : ");
        String res = connection.exeQuerywithNumber(1, Integer.MAX_VALUE).trim();
        System.out.println(res);
        boolean hasSubscribedChannel = true;
        if (res.equals("")) {
            hasSubscribedChannel = false;
        }

        String[] temp = connection.exeQuery(1, Integer.MAX_VALUE).split("\n");
        System.out.print("Do you want to view Channel? (Y/N): ");
        char viewChannel = sc.next().charAt(0);
        if (viewChannel == 'Y') {
            if (!hasSubscribedChannel) {
                System.out.println("You do not have a subscribed channel.");
                viewSubscribedChannel();
            }
            System.out.print("Enter Channel Number: ");
            int channelNum = sc.nextInt() - 1;
            connection.setQuery("select idKanal from Kanal where namaKanal = '" + temp[channelNum].trim() + "'");
            String idKanal = connection.exeQuery(1, 1).trim();
            connection.setQuery("select top 1 idKonten from Konten where idKanal = '" + idKanal + "'");
            viewChannelSignedIn(Integer.parseInt(connection.exeQuery(1, 1).trim()));
        } else {
            if (currentUserType == 0)
                menuAfterChannelCreatedGroup();
            else if (currentUserType == 1)
                menuAfterChannelCreatedIndividu();
            else
                menuAfterSignIn();
        }
    }

    public static void menuAfterChannelCreatedIndividu() {
        System.out.println("\n\n----------Welcome, " + currentUsername + "!----------");
        System.out.println("[1] : View Your Channel (Individual)");
        System.out.println("[2] : Watch Video");
        System.out.println("[3] : View Subcribed Channel");
        System.out.println("[4] : Sign Out");
        System.out.println("--------------------------------------");
        System.out.print("Please Select Menu: ");
        int pilihan = sc.nextInt();

        switch (pilihan) {
            case 1:
                channelIndividual();
                break;
            case 2:
                watchSignin(1);
                break;
            case 3:
                viewSubscribedChannel();
                break;
            case 4:
                currentUserType = -1;
                menuNotSignedIn();
                break;
        }

    }

    public static void menuAfterChannelCreatedGroup() {
        System.out.println("\n\n----------Welcome, " + currentUsername + "!----------");
        System.out.println("[1] : View Your Channel (Group)");
        System.out.println("[2] : Watch Video");
        System.out.println("[3] : View Subcribed Channel");
        System.out.println("[4] : Sign Out");
        System.out.println("--------------------------------------");
        System.out.print("Please Select Menu: ");
        int pilihan = sc.nextInt();

        switch (pilihan) {
            case 1:
                channelGroup();
                break;
            case 2:
                watchSignin(0);
                break;
            case 3:
                viewSubscribedChannel();
                break;
            case 4:
                currentUserType = -1;
                menuNotSignedIn();
                break;
        }
    }

    // 0 : menu group, 1 individu, 2 signIn,
    public static void watchSignin(int pembeda) {
        int id = 0;
        connection.setQuery("Select MAX(idKonten) from Konten");
        int max = Integer.parseInt(connection.exeQuery(1, 1).trim());
        while (true) {
            connection.setQuery(
                    "Select idKonten, judul, durasiVideo, namaKanal from Konten JOIN Kanal ON Konten.idKanal = Kanal.idKanal where idKonten > "
                            + id
                            + "AND status_Penghapusan = 0");
            System.out.println("\n\nNo-----Title--------------Duration---------------Channel----------");
            connection.printQuery(4, 3);
            System.out.println("------------------------------------------------------------------");
            System.out.println("[1] = Previous Page");
            System.out.println("[2] = Next Page");
            System.out.println("[3] = Choose Video");
            System.out.print("Please Select Menu: ");
            int choice = sc.nextInt();
            if (choice == 1) {
                if (id > 0) {
                    id -= 3;
                } else {
                    System.out.println("Already at the first page.");
                }
            } else if (choice == 2) {
                if (id < max) {
                    id += 3;
                } else {
                    System.out.println("Already at the last page.");
                }
            } else if (choice == 3) {
                break;
            }
        }

        System.out.println("Which one do you want to watch?");
        System.out.print("Video Number: ");
        int pilihan = sc.nextInt();
        videoSignIn(pilihan, pembeda);
    }

    public static void watchSignInOneChannel(String idKanal) {
        int page = 0;
        connection.setQuery(
                "SELECT COUNT(idKonten) FROM Konten WHERE idKanal = " + idKanal + " AND status_Penghapusan = 0");
        int total = Integer.parseInt(connection.exeQuery(1, 1).trim());
        int maxPage = (int) Math.ceil(total / 3.0);

        while (true) {
            int offset = page * 3;
            int startRow = offset + 1;
            int endRow = startRow + 3 - 1;
            connection.setQuery(
                    "SELECT idKonten, judul, durasiVideo FROM ("
                            + "SELECT idKonten, judul, durasiVideo, "
                            + "ROW_NUMBER() OVER (ORDER BY idKonten) AS row_num "
                            + "FROM Konten WHERE idKanal = " + idKanal + " AND status_Penghapusan = 0"
                            + ") AS numbered "
                            + "WHERE row_num BETWEEN " + startRow + " AND " + endRow);

            System.out.println("\n\nNo----Title-------------Duration----");
            connection.printQuery(3, 3);
            System.out.println("------------------------------------");
            System.out.println("[1] = Previous Page");
            System.out.println("[2] = Next Page");
            System.out.println("[3] = Choose Video");
            System.out.print("Please Select Menu: ");
            int choice = sc.nextInt();
            if (choice == 1) {
                if (page > 0) {
                    page--;
                } else {
                    System.out.println("Already at the first page.");
                }
            } else if (choice == 2) {
                if (page < maxPage - 1) {
                    page++;
                } else {
                    System.out.println("Already at the last page.");
                }
            } else if (choice == 3) {
                break;
            }
        }

        System.out.println("Which one do you want to watch?");
        System.out.print("Video Number: ");
        int pilihan = sc.nextInt();
        videoSignIn(pilihan, currentUserType);
    }

    public static void videoSignIn(int choice, int pembeda) {
        connection.setQuery("select pathVideo from Konten where idKonten = " + choice);
        String path = connection.exeQuery(1, 1).trim();
        File videoFile = new File(path);

        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(videoFile);
            } catch (IOException e) {
                System.out.println("Video unreachable.");
            }
        }
        connection.setQuery("select judul from Konten where idKonten = '" + choice + "'");
        String judul = connection.exeQuery(1, 1).trim();
        connection.setQuery(
                "insert into Menonton values(" + choice + ",'" + currentUserId + "','" + LocalDate.now() + "')");
        connection.exeInsertQuery();
        while (true) {
            System.out.println("\n\n---------------------------------");
            System.out.printf("Now Playing: %s...\n", judul);
            System.out.println("---------------------------------");

            System.out.println("[1]: Like");
            System.out.println("[2]: Dislike");
            System.out.println("[3]: Subscribe Channel");
            System.out.println("[4]: View Channel");
            System.out.println("[5]: Comment");
            System.out.println("[6]: View All Comment");
            System.out.println("[7]: Back to Home Page");
            System.out.println("---------------------------------");

            System.out.print("Enter your menu choice: ");
            int pilihan = sc.nextInt();
            String res = "";
            switch (pilihan) {
                case 4:
                    viewChannelSignedIn(choice);
                    break;
                case 7:
                    if (pembeda == 0)
                        menuAfterChannelCreatedGroup();
                    else if (pembeda == 1)
                        menuAfterChannelCreatedIndividu();
                    else
                        menuAfterSignIn();
                    break;
            }
        }
    }

    public static void viewChannelSignedIn(int choice) {
        connection.setQuery("select TOP 1 idKanal from Konten where idKonten = " + choice);
        String idKanal = connection.exeQuery(1, 1).trim();

        connection.setQuery("select namaKanal from Kanal where idKanal = " + idKanal);
        String namaKanal = connection.exeQuery(1, 1).trim();
        System.out.printf("\n\nChannel Name: %s\n", namaKanal);
        System.out.println("[1]: View Video List");
        System.out.println("[2]: View Channel Description");
        System.out.println("[3]: Subscribe");
        System.out.println("[4]: Go to This Channel Website");
        System.out.println("[5]: Go to Home Page");
        System.out.println("--------------------------------");
        System.out.print("Please select menu: ");
        int pilihan = sc.nextInt();
        switch (pilihan) {
            case 1:
                watchSignInOneChannel(idKanal);
                break;
            case 2:
                viewChannelDescription(idKanal, namaKanal, choice);
                break;
            case 3:
                connection.setQuery("select status_Penghapusan from Subscribe where idPengguna = '" + currentUserId
                        + "' and idKanal = '" + idKanal + "'");
                String res = connection.exeQuery(1, 1).trim();

                if (res.equals("")) {
                    connection.setQuery("insert into Subscribe values ('" + idKanal + "', '" + currentUserId + "', '"
                            + LocalDate.now() + "',null, '0')");
                    connection.exeInsertQuery();

                    System.out.println("You have successfully subscribed the video.");
                } else if (res.equals("1")) {
                    connection.setQuery("update Subscribe set status_Penghapusan = '0', tanggalSubscribe ='"
                            + LocalDate.now() + "' where idPengguna = '" + currentUserId + "' and idKanal = '"
                            + idKanal + "'");
                    connection.exeInsertQuery();

                    System.out.println("You have successfully subscribed the video.");
                } else {
                    connection.setQuery("update Subscribe set status_Penghapusan = '1', tanggalUnsubscribe ='"
                            + LocalDate.now() + "' where idPengguna = '" + currentUserId + "' and idKanal = '"
                            + idKanal + "'");
                    connection.exeInsertQuery();

                    System.out.println("You have successfully un-subscribed the video.");
                }
                viewChannelSignedIn(choice);
                break;
            case 4:
                connection.setQuery("select website from Kanal where idKanal = " + idKanal);
                String website = connection.exeQuery(1, 1).trim();

                if (Desktop.isDesktopSupported()) {
                    try {
                        URI link = new URI(website);
                        Desktop.getDesktop().browse(link);
                    } catch (IOException e) {
                        System.out.println("Video unreachable.");
                        videonotSignIn(choice);
                    } catch (URISyntaxException e) {
                        System.out.println("Website is unreachable.");
                        videonotSignIn(choice);
                    }
                }
                viewChannelSignedIn(choice);
                break;
            case 5:
                if (currentUserType == 0)
                    menuAfterChannelCreatedGroup();
                else if (currentUserType == 1)
                    menuAfterChannelCreatedIndividu();
                else
                    menuAfterSignIn();
                break;
            default:
                System.out.println("Invalid choice.");
                viewChannelSignedIn(choice);
                break;
        }
    }

    /*
     * GROUP
     * KANAL INDIVIDU
     */
    public static void createChannel() {
        System.out.println("\n\n-------------Create Channel----------------");
        System.out.println("Please enter following information:");
        System.out.print("Channel Name             : ");
        String channelName = sc.next() + sc.nextLine();

        // boolean bg = false;
        // while (!bg) {
        // System.out.println("Upload Background :");
        // System.out.print("Press 1 to Upload Background File: ");
        // int confirm = sc.nextInt();
        // if (confirm == 1) {
        // System.out.println("Background Image Uploaded Successfully.");
        // bg = true;
        // } else {
        // System.out.println("Background Upload Failed. Please Re-Upload.");
        // }
        // }
        System.out.print("Set Description       : ");
        String description = sc.next() + sc.nextLine();

        System.out.print("Website               : ");
        String website = sc.next();

        // System.out.println("channel"+channelName);
        // System.out.println("desc "+description);
        // System.out.println("website"+website);
        // System.out.println("Kanal"+maxIdKanal);
        // System.out.println("Konten "+maxIdKonten);
        // System.out.println("pengguna "+maxIdPengguna);
        System.out.println("--------------------------------------------");
        System.out.print("Create Channel? (Y / N): ");
        String ans = sc.next().trim();
        if (ans.equalsIgnoreCase("Y")) {
            connection.setQuery("insert into Kanal values ('" + maxIdKanal + "', '" + channelName + "', '" + description
                    + "', '" + LocalDate.now() + "', '" + website + "')");
            connection.exeInsertQuery();
            connection.setQuery("insert into KanalIndividu values ('" + maxIdKanal + "')");
            connection.exeInsertQuery();
            connection.setQuery(
                    "update Pengguna set jabatan = 'Owner', tipe_Pengguna = '1', idKanal = '" + maxIdKanal
                            + "' where namaP = '" + currentUsername + "'");
            connection.exeInsertQuery();
            currentUserRole = 0; // owner
            maxIdKanal++;

            System.out.println("Your Channel Has Been Successfully Created.");

            menuAfterChannelCreatedIndividu();
        } else {
            menuAfterSignIn();
        }

    }

    public static void channelIndividual() {
        System.out.println("\n\n----------Welcome to Your Channel----------");
        System.out.println("[1] : View Video List");
        System.out.println("[2] : View Dashboard");
        System.out.println("[3] : Upload Video");
        System.out.println("[4] : Back to Menu");
        System.out.println("--------------------------------------------");
        System.out.print("Please Select Menu: ");
        int pilihan = sc.nextInt();

        connection.setQuery("select idKanal from Pengguna where idPengguna = '" + currentUserId + "'");
        String idKanal = connection.exeQuery(1, 1).trim();
        switch (pilihan) {
            case 1:
                viewVideoList(0, idKanal);
                break;
            case 2:
                viewDashboardIndividual(idKanal);
                break;
            // case 3:
            //     uploadVideo(0, idKanal);
            //     break;
            default:
                menuAfterChannelCreatedIndividu();
                break;
        }
    }

    public static void viewDashboardIndividual(String idKanal) {
        System.out.println("\n\n----------Dashboard----------");
        System.out.println("[1] : View All Subscriber(s)");
        System.out.println("[2] : View Channel Analysis");
        System.out.println("[3] : Back to Channel");
        System.out.println("---------------------------");
        System.out.print("Please select menu: ");
        int pilihan = sc.nextInt();

        switch (pilihan) {
            case 1:
                viewSubscriberList(0, idKanal);
                break;
            case 2:
                viewChannelAnalysis(0, idKanal);
                break;
            case 3:
                channelIndividual();
                break;

        }

    }

    /*
     * GROUP
     * KANAL GROUP
     */
    public static void createGroupChannel() {
        System.out.println("\n\n-------------Create Channel----------------");
        System.out.println("Please enter following information:");
        System.out.print("Channel Name          : ");
        String channelName = sc.next() + sc.nextLine();

        // boolean bg = false;
        // while (!bg) {
        // System.out.println("Upload Background :");
        // System.out.print("Press 1 to Upload Background File: ");
        // int confirm = sc.nextInt();
        // if (confirm == 1) {
        // System.out.println("Background Image Uploaded Successfully.");
        // bg = true;
        // } else {
        // System.out.println("Background Upload Failed. Please Re-Upload.");
        // }
        // }

        System.out.print("Set Description       : ");

        String description = sc.next() + sc.nextLine();
        // System.out.println();
        System.out.print("Website               : ");
        String website = sc.next();
        System.out.println();
        System.out.println("-----Create a brand account-----");
        System.out.print("Email Address     : ");
        String email = sc.next();
        System.out.print("Brand Name        : ");
        String brandAccount = sc.next() + sc.nextLine();
        System.out.print("Password          : ");
        String password = sc.next();
        System.out.print("Confirm password  : ");
        String confirmPass = sc.next();

        while (!confirmPass.equals(password)) {
            System.out.println("-----------------------------------------------------");
            System.out.println(" Password mismatch, please enter your password again");
            System.out.println("-----------------------------------------------------");

            System.out.printf("Email Address     : %s\n", email);
            System.out.print("Password          : ");
            password = sc.next();
            System.out.print("Confirm password  : ");
            confirmPass = sc.next();
        }
        System.out.println("--------------------------------------------");
        System.out.print("Create Channel? (Y / N): ");
        String ans = sc.next();
        // idKanal int NOT NULL PRIMARY KEY,
        // namaKanal varchar(75),
        // deskripsiKanal varchar(150),
        // tanggal_PembuatanKanal date
        if (ans.equalsIgnoreCase("Y")) {

            // idKanal int FOREIGN KEY REFERENCES Kanal (idKanal),
            // jumlah_Anggota int,
            // namaBrand varchar(50),
            // passBrand varchar(75),
            // emailBrand varchar(75)

            connection.setQuery("insert into Kanal values (' " + maxIdKanal + "','" + channelName + "','" + description
                    + "','" + LocalDate.now() + "', '" + website + "')");
            connection.exeInsertQuery();

            connection.setQuery("insert into KanalGroup values ('" + maxIdKanal + "', '1', '" + brandAccount
                    + "','" + password + "','" + email + "')");
            connection.exeInsertQuery();

            connection.setQuery(
                    "update Pengguna set jabatan = 'Owner', tipe_Pengguna = '1', idKanal = '" + maxIdKanal
                            + "' where namaP = '" + currentUsername + "'");
            connection.exeInsertQuery();
            System.out.println("Your Group Channel Has Been Successfully Created.");
            currentUserRole = 0; // owner
            maxIdKanal++;
            menuAfterChannelCreatedGroup();
        } else {
            menuAfterSignIn();
        }
    }

    public static void channelGroup() {
        System.out.println("\n\n----------Channel Group----------");
        System.out.println("[1] : View Video List");
        System.out.println("[2] : View Dashboard");
        System.out.println("[3] : Upload Video");
        System.out.println("[4] : View Member");
        System.out.println("[5] : Add Member");
        System.out.println("[6] : Back To Menu");
        System.out.println("---------------------------------");
        System.out.print("Please Select Menu: ");
        int pilihan = sc.nextInt();

        connection.setQuery("select idKanal from Pengguna where idPengguna = '" + currentUserId + "'");
        String idKanal = connection.exeQuery(1, 1).trim();

        switch (pilihan) {
            case 1:
                viewVideoList(1, idKanal);
                break;
            case 2:
                viewDashboardGroup(idKanal);
                break;
            // case 3:
            //     uploadVideo(1, idKanal);
            //     break;
            case 4:
                viewMemberList(idKanal);
                break;
            default:
                menuAfterChannelCreatedGroup();
                break;
        }
    }

    public static void addGroupMember(String idKanal) {
        System.out.println("\n\n----------Add a group member----------");
        System.out.print("Name : ");
        String nama = sc.next();
        System.out.print("Email: ");
        String email = sc.next();
        connection.setQuery("select namaP from Pengguna where namaP='" + nama + "'and email='" + email + "'");
        String namaPengguna = connection.exeQuery(1, 1).trim();
        if (namaPengguna.equals("")) {
            System.out.println("This user is not registered.");
            addGroupMember(idKanal);
        }
        connection.setQuery("select idKanal from Pengguna where namaP='" + nama + "'and email='" + email + "'");
        String temp = connection.exeQuery(1, 1).trim();
        if (!temp.equalsIgnoreCase("NULL")) {
            System.out.println("This user has already created a channel / a content creator in another channel.");
            addGroupMember(idKanal);
        }
        String[] jabatan = { "Manager", "Editor", "Editor (Limited)", "Subtitle Editor", "Viewer" };

        System.out.println("Role :");
        for (int i = 1; i <= 5; i++) {
            System.out.printf("[%d] : %s\n", i, jabatan[i - 1]);
        }
        System.out.println("----------------------------------------");

        System.out.print("Please select new member Role: ");
        int newRole = sc.nextInt() - 1;
        connection.setQuery("update Pengguna set tanggal_Undang  = '" + LocalDate.now() + "' ,jabatan = '"
                + jabatan[newRole] + "',tipe_Pengguna='1', idKanal = '" + idKanal + "' where namaP ='" + namaPengguna
                + "'");
        System.out.println("Add Member (Y/N)");
        String memberyesno = sc.next();

        if (memberyesno.equalsIgnoreCase("Y")) {
            connection.exeInsertQuery();
            connection.setQuery(
                    "update KanalGroup set jumlah_Anggota = jumlah_Anggota + 1 where idKanal = '" + idKanal + "'");
            connection.exeInsertQuery();
            System.out.println("New Member has been successfully added");
            viewMemberList(idKanal);
        } else {
            viewMemberList(idKanal);
        }
    }

    public static void viewMemberList(String idKanal) {
        connection.setQuery("select namaP, jabatan from Pengguna where idKanal ='" + idKanal + "'");
        System.out.println("No-----Member Name------Role---------");
        connection.printQuerywithNumber(2, Integer.MAX_VALUE);
        System.out.println("-------------------------------------");
        System.out.println("Do you want to edit member? (Y/N): ");
        if (sc.next().charAt(0) == 'N') {
            channelGroup();
        } else {
            connection.setQuery("select namaP from Pengguna where idKanal ='" + idKanal + "'");
            String[] memberList = connection.exeQuery(1, Integer.MAX_VALUE).split("\n");
            System.out.print("Select Member to Edit: ");
            int pilihan = sc.nextInt();

                editGroupMember(memberList[pilihan - 1], idKanal);

                System.out.print("Back to Channel? (Y/N): ");
                if (sc.next().charAt(0) == 'Y') {
                    channelGroup();
                } else {
                    viewMemberList(idKanal);
                }
        }
    }

    public static void editGroupMember(String memberName, String idKanal) {
        System.out.printf("\n\n-------Editing %s--------\n", memberName);
        System.out.println("[1] : Edit Member Role");
        System.out.println("[2] : Remove Member");
        System.out.println("--------------------------------");
        System.out.print("Please select menu: ");
        int choice = sc.nextInt();

        switch (choice) {
            case 1:
                String[] jabatan = { "Manager", "Editor", "Editor (Limited)", "Subtitle Editor", "Viewer" };

                System.out.println("Role :");
                System.out.println("----------------------------------------");

                for (int i = 1; i <= 5; i++) {
                    System.out.printf("[%d] : %s\n", i, jabatan[i - 1]);
                }
                System.out.println("----------------------------------------");

                System.out.println("Please Select New Role :");

                int choiceeee = sc.nextInt();
                connection.setQuery("update Pengguna set jabatan = '" + jabatan[choiceeee - 1] + "' where namaP ='"
                        + memberName + "' and idKanal = '" + idKanal + "'");
                connection.exeInsertQuery();
                System.out.println("Member role has been updated.");
                viewMemberList(idKanal);
                break;
            case 2:
                connection.setQuery(
                        "update Pengguna set jabatan = null, tanggal_Undang = null, idKanal = null where namaP ='"
                                + memberName + "' and idKanal = '" + idKanal + "'");
                connection.exeInsertQuery();
                connection.setQuery(
                        "update KanalGroup set jumlah_Anggota = jumlah_Anggota -1 where idKanal ='" + idKanal + "'");
                connection.exeInsertQuery();
                System.out.println("Member has been successfully removed.");

                viewMemberList(idKanal);
                break;
        }
    }

    public static void viewDashboardGroup(String idKanal) {
        System.out.println("\n\n-------Dashboard Group---------");
        System.out.println("[1] : View All Subscriber(s)");
        System.out.println("[2] : View Channel Analysis");
        System.out.println("[3] : Back to Channel");
        System.out.println("-------------------------------");

        System.out.print("Please Select Menu: ");

        int pilihan = sc.nextInt();

        switch (pilihan) {
            case 1:
                viewSubscriberList(1, idKanal);
                break;
            case 2:
                viewChannelAnalysis(1, idKanal);
                break;
            case 3:
                channelGroup();
                break;

        }

    }

    /*
     * GROUP
     * METHOD SHARING
     */
    public static void viewSubscriberList(int pembeda, String idKanal) {
        System.out.println("\n\n----------List of Subscriber(s)-----------");
        connection.setQuery(
                "select Pengguna.namaP from Subscribe inner join Kanal on Subscribe.idKanal = Kanal.idKanal inner join Pengguna on Subscribe.idPengguna=Pengguna.idPengguna where Subscribe.idKanal ="
                        + idKanal);
        System.out.print(connection.exeQuery(1, Integer.MAX_VALUE));
        System.out.println("---------------------------------------");
        while (true) {
            System.out.print("Back to Dashboard? (Y / N) : ");
            char choice = sc.next().charAt(0);
            if (choice == 'Y' || choice == 'y') {
                if (pembeda == 0) {
                    viewDashboardIndividual(idKanal);
                    break;
                } else {
                    viewDashboardGroup(idKanal);
                    break;
                }
            }
        }

    }

    public static void viewVideoList(int pembeda, String idKanal) {
        System.out.println("List of Your Videos : ");

        int page = 0;
        connection.setQuery(
                "SELECT COUNT(idKonten) FROM Konten WHERE idKanal = " + idKanal + " AND status_Penghapusan = '0'");
        int total = Integer.parseInt(connection.exeQuery(1, 1).trim());
        int maxPage = (int) Math.ceil(total / 3.0);
        boolean isBack = false;
        String[] output;
        while (true) {
            int offset = page * 3;
            int startRow = offset + 1;
            int endRow = startRow + 3 - 1;
            connection.setQuery(
                    "SELECT idKonten, judul, durasiVideo FROM ("
                            + "SELECT idKonten, judul, durasiVideo, "
                            + "ROW_NUMBER() OVER (ORDER BY idKonten) AS row_num "
                            + "FROM Konten WHERE idKanal = " + idKanal + " AND status_Penghapusan = 0"
                            + ") AS numbered "
                            + "WHERE row_num BETWEEN " + startRow + " AND " + endRow);

            System.out.println("\n\nNo--------Title---------Duration----");
            connection.printQuery(3, 3);
            System.out.println("------------------------------------");
            System.out.println("[1] : Previous Page");
            System.out.println("[2] : Next Page");
            System.out.println("[3] : Choose Video");
            System.out.println("[4] : Back");
            System.out.print("Please Select Menu: ");
            int choice = sc.nextInt();

            if (choice == 1) {
                if (page > 0) {
                    page--;
                } else {
                    System.out.println("Already at the first page.");
                }
            } else if (choice == 2) {
                if (page < maxPage - 1) {
                    page++;
                } else {
                    System.out.println("Already at the last page.");
                }
            } else if (choice == 3) {
                break;
            } else if (choice == 4) {
                isBack = true;
                if (pembeda == 0) {
                    channelIndividual();
                } else {
                    channelGroup();
                }
                break;
            } else {
                System.out.println("Invalid choice.");
            }
        }

        if (!isBack) {
            System.out.print("Please select video: ");
            int pilihan = sc.nextInt();

            manageVideo(pilihan, pembeda);
        }
    }

    public static void viewChannelAnalysis(int pembeda, String idKanal) {
        System.out.println("\n\n---------------------Channel Analysis-------------------------");

        connection.setQuery("select top 1 judul from (select idKonten, judul from Konten where idKanal = '" + idKanal
                + "') as KontenKanal JOIN Menonton ON KontenKanal.idKonten = Menonton.idKonten group by judul");
        String judul = connection.exeQuery(1, 1).trim();
        if (judul.length() == 0)
            judul = "-";
        System.out.println("[1] : Most Watched Video                : " + judul);

        connection.setQuery(
                "select TOP 1 judul from (select judul, count(judul) as JumlahLike from Konten JOIN Likes ON Konten.idKonten = Likes.idKonten JOIN Kanal ON Kanal.idKanal = Konten.idKanal where Kanal.idKanal = '"
                        + idKanal + "' group by judul) as HimpJmlhLike");
        judul = connection.exeQuery(1, 1);
        if (judul.length() == 0)
            judul = "-";
        System.out.println("[2] : Most Liked Video                  : " + judul);

        connection.setQuery(
                "select judul FROM Konten JOIN Komen ON Konten.idKonten = Komen.idKonten JOIN Kanal ON Kanal.idKanal = Konten.idKanal WHERE Kanal.idKanal = '"
                        + idKanal + "'");
        judul = connection.exeQuery(1, 1);
        if (judul.length() == 0)
            judul = "-";
        System.out.println("[3] : Most Commented Video              : " + judul);
        // System.out.println("[4] : Highest Watched Time Subscriber : ");
        System.out.println("--------------------------------------------------------------");

        while (true) {
            System.out.print("Back? (Y/N): ");
            char choice = sc.next().charAt(0);
            if (choice == 'Y' || choice == 'y') {
                if (pembeda == 0) {
                    viewDashboardIndividual(idKanal);
                    break;

                } else {
                    viewDashboardGroup(idKanal);
                    break;
                }
            }
        }
    }

    public static void manageVideo(int choice, int pembeda) {
        System.out.printf("\n\nVideo %d\n", choice);
        System.out.println("----------Video Management----------");
        System.out.println("[1] : Take Down");
        System.out.println("[2] : Edit Video");
        System.out.println("[3] : Back");
        System.out.println("------------------------------------");
        System.out.print("Please Select Option: ");

        int pilihan = sc.nextInt();
        connection.setQuery("select idKanal from Pengguna where idPengguna = '" + currentUserId + "'");
        String idKanal = connection.exeQuery(1, 1).trim();

        switch (pilihan) {

            default:
                viewVideoList(pembeda, idKanal);
                break;
        }
    }
}

/*
 * yang sudah dibuat
 *
 * 1 : Manager, 2 : Editor, 3 : Editor Limited, 4 : Subtitle Editor, 5 : Viewer
 *
 * pembeda 0 individu, 1 grup
 * 
 * +++++ BAGIAN YANG NOT SIGN IN +++++
 * - Menu awal
 * - menu not sign in
 * - sign in
 * - register
 * - video not sign in
 * - watch not sign in
 * - view channel not sign in
 *
 *
 * +++++ BAGIAN YANG SUDAH SIGN IN +++++
 * - menu sign in
 * - video sign in
 * - watch sign in
 * - view channel sign in
 * - menu After Channel Created Individu
 * - menu after channel created group
 * - chooseRole
 * - view subscribed channel
 * 
 * 
 * +++++ KANAL INDIVIDU +++++
 * - create channel
 * - channel individual
 * - view dashboard individual
 * 
 * +++++ KANAL GROUP +++++
 * - create channel grop
 * - create brand account
 * - channel group
 * - add group member
 * - edit group member
 * - view group member
 * - view dashboard group
 * 
 * +++++ METHOD SHARING +++++
 * - view Subscriber List
 * - view video list
 * - upload video
 * - view Channel Analysis
 * - manage video
 */

/*
Implementasi Hak Akses Editor Limited
pada sprint ke 3 ini, kelompok akan membuat Berbagai jenis implementasi kode kode yang akan berguna bagi
UI CLI Youtube nantinya akan berisi Upload video, edit video, remove video, sistem sistem hak akses
hak owner juga akan dijelaskan pada kode ini. Kemudian ada sistem hak akses subtitle dan hak akses editor,
viewer, DLL.
Sprint ini akan diakhiri oleh testing oleh 3 tester dan 1 Backend Developer.
*/

// Implementasi sistem hak akses manager
// Sprint 3
// Mengatur hak akses manager agar dapat
// memantau dan mengelola data sesuai
// kewenangan.

