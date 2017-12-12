
import java.io.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import java.math.*;
import javax.xml.parsers.*;
import org.xml.sax.*;

// beinhaltet die Aufgabenstellungs-Lösungen
public class Main {

    // Bietet das Menü zur Steuerung zwischen den Aufgaben
    public static void main(String args[]) throws IOException {

        OracleConnection con;
        BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
        int choice;
        try {
            con = new OracleConnection("dbprak45", "TiMo45");
            do {
                System.out.println("\nBitte wählen Sie einen der folgenden Menüpunkte");
                System.out.println("(1) JDBC Insert");
                System.out.println("(2) Logistik Verwaltung");
                System.out.println("(3) Erfassung Kundenbestellung");
                System.out.println("(0) Beenden\n");
                choice = Integer.parseInt(read.readLine());
                switch (choice) {
                    case 1:
                        nr4(con);
                        break;
                    case 2:
                        nr5(con);
                        break;
                    case 3:
                        nr6(con);
                        break;
                }
            } while (choice != 0);
            con.close();
        } catch (ClassNotFoundException e) {
            Util.stdExceptionOut(e);

            System.exit(40);
        } catch (SQLException e) {
            Util.stdExceptionOut(e);

            System.exit(41);
        }

    }

    // Liest .CSV Datei mit Datensätzen ein und schreibt diese in die Tabelle Artikel der Datenbank
    // Zählt die Anzahl gültiger Einträge und gibt sie aus
    public static void nr4(OracleConnection con) throws FileNotFoundException, IOException {

        String tmp, split[];
        int anzahl = 0;
        File file = new File("ARTIKEL.CSV");
        BufferedReader fileR = new BufferedReader(new FileReader(file));

        // Einlesen der Datensätze
        while (null != (tmp = fileR.readLine())) {
            split = tmp.split(";");
            String spalten[] = {"ARTBEZ", "MGE", "PREIS", "STEU", "EDAT"}, tabelle = "Artikel", werte[];

            // Aufbau des Statements
            for (int i = 0; i < split.length; i += 5) {
                werte = new String[5];
                werte[0] = "'" + split[i + 0] + "'";
                werte[1] = "'" + split[i + 1] + "'";
                werte[2] = split[i + 2];
                werte[3] = split[i + 3];
                werte[4] = "to_date('" + split[i + 4] + "','DD.MM.YYYY')";

                // Ausführen
                try {
                    Util.insert(con, tabelle, spalten, werte);
                    anzahl++;
                } catch (SQLException e) {
                    Util.stdExceptionOut(e);
                }
            }
        }

        System.out.println("\nAnzahl gültiger einträge: " + anzahl + "\n");

    }

    // Bietet das Menü zur Steuerung der Aufgabe 5
    public static void nr5(OracleConnection con) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        int choice;

        do {
            System.out.println("\nBitte wählen sie den entsprechenden Menüpunkt. ");
            System.out.println("(1) Anzeigen aller Artikel");
            System.out.println("(2) Anzeigen aller Lager");
            System.out.println("(3) Anzeigen aller Kunden");
            System.out.println("(4) Anzeigen der Stammdaten eines Artikels");
            System.out.println("(5) Erfassen eines neuen Lagerbestandes");
            System.out.println("(6) anpassen der Menge eines Artikels");
            System.out.println("(7) Versandmeldung");
            System.out.println("(8) Rechnungserstellung");
            System.out.println("(0) Zurück");

            choice = Integer.parseInt(reader.readLine());

            switch (choice) {
                case 1:
                    try {
                        anzeigenAllerArtikel(con);
                    } catch (SQLException e) {
                        Util.stdExceptionOut(e);
                    }

                    break;
                case 2:
                    try {
                        anzeigenAllerLager(con);
                    } catch (SQLException e) {
                        Util.stdExceptionOut(e);
                    }

                    break;

                case 3:
                    try {
                        anzeigenAllerKunden(con);
                    } catch (SQLException e) {
                        Util.stdExceptionOut(e);
                    }

                    break;
                case 4:
                    System.out.println("Bitte Artikelnummer eingeben: ");

                    try {
                        stammdaten(con, reader.readLine());
                    } catch (SQLException e) {
                        Util.stdExceptionOut(e);
                    }

                    break;

                case 5: {
                    String bstnr,
                            artnr,
                            lnr,
                            menge;
                    System.out.println("Bitte Bestandsnummer eingeben: ");
                    bstnr = reader.readLine();
                    System.out.println("Bitte Artikelnummer eingeben: ");
                    artnr = reader.readLine();
                    System.out.println("Bitte Lagernummer eingeben: ");
                    lnr = reader.readLine();
                    System.out.println("Bitte Menge eingeben: ");
                    menge = reader.readLine();
                    try {
                        erfassenLagerbestand(con, bstnr, artnr, lnr, menge);
                    } catch (SQLException e) {
                        Util.stdExceptionOut(e);
                    }
                    System.out.println("Ihre Eingabe war erfolgreich\n");
                    break;
                }

                case 6: {
                    String bstnr,
                            menge;
                    System.out.println("Bitte Bestandsnummer eingeben: ");
                    bstnr = reader.readLine();
                    System.out.println("Bitte neue Menge eingeben: ");
                    menge = reader.readLine();

                    try {
                        updateMenge(con, bstnr, menge);
                    } catch (SQLException e) {
                        Util.stdExceptionOut(e);
                    }
                    break;
                }

                case 7: {
                    System.out.println("Bitte Bestellnummer eingeben: ");
                    String benr = reader.readLine();
                    System.out.println("Bitte Lieferdatum eingeben (dd.mm.yyyy): ");
                    String vdat = reader.readLine();
                    try {
                        if (!versandrueckmeldung(con, benr, vdat)) {
                            System.out.println("Keinen Eintrag gefunden!");
                        }
                    } catch (SQLException | ParseException e) {
                        Util.stdExceptionOut(e);

                    }

                    break;
                }

                case 8: {
                    System.out.println("Bitte geben Sie eine Bestellnummer ein: ");
                    String benr = reader.readLine();
                    try {
                        if (rechnungserstellung(con, benr) == -1) {
                            System.out.println("Keinen Eintrag gefunden!");
                        }
                    } catch (SAXParseException ep) {
                        System.out.println("Parser meldet FEHLER : " + ep.toString());
                        System.out.println("an der Entity        : " + ep.getPublicId());
                        System.out.println("Zeile,Spalte         : " + ep.getLineNumber() + "," + ep.getColumnNumber());
                    } catch (ParserConfigurationException | SAXException | SQLException | ParseException e) {
                        Util.stdExceptionOut(e);
                    }
                    break;
                }
            }
        } while (choice != 0);
    }
    // Methode zum Erfassen einer Kundenbestellung

    public static void nr6(OracleConnection con) throws IOException, SQLException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        int bestnr = benrNext(con);
        ArrayList<String[]> kunde, artikel, lagerbestand, kubest;

        // Einlesen und ueberpruefen der Daten auf Richtigkeit 
        System.out.println("----------Automatisches Bestellsystem----------\n\n");

        do {
            System.out.println("Bitte geben Sie die Kundenummer ein\n");
            String spalten[] = {"KNR", "KNAME", "PLZ", "ORT", "STRASSE"};
            kunde = con.select("KUNDE", spalten, "KNR = " + reader.readLine());
        } while (kunde.size() < 1);

        do {
            System.out.println("Bitte geben Sie die Artikelnummer ein\n");
            String spalten[] = {"ARTNR", "PREIS", "ARTBEZ"};
            artikel = con.select("ARTIKEL", spalten, "ARTNR = " + reader.readLine());
        } while (artikel.size() < 1);

        System.out.println("Bitte geben sie die Bestellmenge ein");
        int mengeEin = Integer.parseInt(reader.readLine());
        int mengeLag = stammdaten(con, artikel.get(0)[0]);

        String spaltenSel[] = {"BSTNR", "MENGE"};
        lagerbestand = con.select("LAGERBESTAND", spaltenSel, "ARTNR = " + artikel.get(0)[0]);

        // Prüfen ob eine ausreichende Menge in den Lagern liegt 
        if (mengeEin <= mengeLag) {
            int mengeEinTmp = mengeEin, mengeTmp;
            for (String arr[] : lagerbestand) {
                mengeTmp = Integer.parseInt(arr[1]);
                if (mengeTmp >= mengeEinTmp) {
                    mengeTmp = mengeEinTmp;
                }
                String spaltenUpd[] = {"MENGE"}, werte[] = {arr[1] + " - " + mengeTmp};
                Util.update(con, "LAGERBESTAND", spaltenUpd, werte, "BSTNR = " + arr[0]);
                mengeEinTmp -= mengeTmp;

                if (mengeEinTmp == 0) {
                    break;
                }
            }
            // Insert auf KUBEST
            String spalten[] = {"BENR", "KNR", "ARTNR", "BMENGE", "BDAT", "LDAT", "STATUS", "RBET"},
                    werte[] = {"" + bestnr, kunde.get(0)[0], artikel.get(0)[0], "" + mengeEin, "sysdate", "sysdate+14", "" + 1, artikel.get(0)[1] + " * " + mengeEin};

            Util.insert(con, "KUBEST", spalten, werte);
            spalten = new String[]{"LDAT", "RBET"};
            kubest = con.select("KUBEST", spalten, "BENR = " + bestnr);

            // Schreiben des Lieferscheins
            StringBuilder line = new StringBuilder();
            line.append("NAME: ").append(kunde.get(0)[1]).append("\n");
            line.append("PLZ: ").append(kunde.get(0)[2]).append("\n");
            line.append("ORT: ").append(kunde.get(0)[3]).append("\n");
            line.append("STRASSE: ").append(kunde.get(0)[4]).append("\n\n");
            line.append("Artikel: ").append(artikel.get(0)[2]).append("\tMenge: ").append(mengeEin).append("\n");
            line.append("spätestes Lieferdatum: ").append(kubest.get(0)[0]).append("\n");
            line.append("Betrag: ").append(kubest.get(0)[1]);

            Util.writeFile("AB" + kunde.get(0)[0] + "B" + bestnr + ".txt", line.toString());

            System.out.println("----------Lieferschein wurde erstellt----------");
            System.out.println("Die Rechnungsnummer lautet: " + bestnr);

        } else {
            int dx = mengeEin - mengeLag;
            System.out.println("Fehlmenge: " + dx);
            System.out.println("Nicht genügend Lagerbestand vorhanden!");

        }
    }

    public static boolean versandrueckmeldung(OracleConnection con, String benr, String vdat) throws IOException, ParseException, SQLException {

        String spalten[] = {"LDAT", "UET", "GUTS", "RBET"};
        String where = "BENR = " + benr;
        ArrayList<String[]> ldat;
        String vdatToDate = "TO_DATE('" + vdat + "','DD.MM.YYYY')";
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        java.util.Date vdatDate = dateFormat.parse(vdat);

        ldat = con.select("KUBEST", spalten, where);
        if (ldat.size() == 1) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date ldatDate = dateFormat.parse(ldat.get(0)[0]);
            if (vdatDate.compareTo(ldatDate) <= 0) {
                spalten = new String[]{"LDAT", "UET", "GUTS", "STATUS"};
                String werte[] = {vdatToDate, "0", "0", "2"};
                Util.update(con, "KUBEST", spalten, werte, where);
            } else {
                spalten = new String[]{"UET", "STATUS"};
                String werte[] = {vdatToDate + " - LDAT", "2"};
                Util.update(con, "KUBEST", spalten, werte, where);

                spalten = new String[]{"GUTS", "LDAT"};
                werte = new String[]{"(RBET * 0.05 * UET) / 365", vdatToDate};
                Util.update(con, "KUBEST", spalten, werte, where);
            }
            System.out.println("\n------Versanddaten aufgenommen!--------\n");
            return true;
        } else {
            return false;
        }

    }

    public static int rechnungserstellung(OracleConnection con, String benr) throws SQLException, ParseException, IOException, ParserConfigurationException, SAXException {

        ArrayList<String[]> kubest;
        String spaltenKubest[] = {"BENR", "STATUS", "KNR", "ARTNR", "BMENGE", "RBET", "LDAT", "GUTS"},
                rech[] = new String[11],
                whereKubest = "BENR = " + benr;

        kubest = con.select("KUBEST", spaltenKubest, whereKubest);
        if (kubest.size() == 1) {
            if (kubest.get(0)[1].equals("2")) {
                String spaltenKunde[] = {"KNAME", "PLZ", "ORT", "STRASSE"},
                        whereKunde = "KNR = " + kubest.get(0)[2],
                        spaltenArtikel[] = {"ARTBEZ"},
                        whereArtikel = "ARTNR = " + kubest.get(0)[3];
                ArrayList<String[]> artikel, kunde;
                artikel = con.select("ARTIKEL", spaltenArtikel, whereArtikel);
                kunde = con.select("KUNDE", spaltenKunde, whereKunde);

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Calendar cal = Calendar.getInstance();
                cal.setTime(dateFormat.parse(kubest.get(0)[6]));
                cal.add(Calendar.DATE, 14);

                // RENR
                rech[0] = kubest.get(0)[0] + "R";
                // RDAT
                rech[1] = dateFormat.format(cal.getTime());
                // KNR
                rech[2] = kubest.get(0)[2];
                // KNAME
                rech[3] = kunde.get(0)[0];
                // PLZ
                rech[4] = kunde.get(0)[1];
                // ORT
                rech[5] = kunde.get(0)[2];
                // Strasse
                rech[6] = kunde.get(0)[3];
                // ARTNR
                rech[7] = kubest.get(0)[4];
                // ARTBEZ
                rech[8] = artikel.get(0)[0];
                // BMENGE
                rech[9] = kubest.get(0)[4];
                // ERBET
                rech[10] = new BigDecimal(kubest.get(0)[5]).subtract(new BigDecimal(kubest.get(0)[7])).setScale(2, RoundingMode.CEILING).toString();

                String spaltenRechnung[] = {"BENR", "RDAT", "ERBET"},
                        werteRechnung[] = {kubest.get(0)[0], "TO_DATE('" + rech[1] + "','YYYY-MM-DD HH24:MI:SS')", rech[10]};
                Util.insert(con, "RECHNUNG", spaltenRechnung, werteRechnung);

                StringBuilder line = new StringBuilder();
                line.append("Rechnungsnummer: ").append(rech[0]);
                line.append("\nRechnungsdatum: ").append(rech[1]);

                line.append("\n\nKundeninformationen: ");
                line.append("\n\tKundennummer: ").append(rech[2]);
                line.append("\n\tName: : ").append(rech[3]);
                line.append("\n\tPLZ: ").append(rech[4]);
                line.append("\n\tOrt: : ").append(rech[5]);
                line.append("\n\tStrasse: ").append(rech[6]);

                line.append("\n\nArtikelinformationen: ");
                line.append("\n\tArtikelnummer: ").append(rech[7]);
                line.append("\n\tBezeichnung: ").append(rech[8]);
                line.append("\n\tMenge: ").append(rech[9]);
                line.append("\n\nBetrag: ").append(rech[10]);

                Util.writeFile("RECH-" + kubest.get(0)[0] + ".txt", line.toString());

                line = new StringBuilder();
                line.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
                line.append("\n<!DOCTYPE RECH SYSTEM \"rechnung.dtd\">");
                line.append("\n<RECH>");
                line.append("\n\t<RENR>").append(rech[0]).append("</RENR>");
                line.append("\n\t<RDAT>").append(rech[1]).append("</RDAT>");
                line.append("\n\t<KNR>").append(rech[2]).append("</KNR>");
                line.append("\n\t<KNAME>").append(rech[3]).append("</KNAME>");
                line.append("\n\t<PLZ>").append(rech[4]).append("</PLZ>");
                line.append("\n\t<ORT>").append(rech[5]).append("</ORT>");
                line.append("\n\t<Strasse>").append(rech[6]).append("</Strasse>");
                line.append("\n\t<ARTNR>").append(rech[7]).append("</ARTNR>");
                line.append("\n\t<ARTBEZ>").append(rech[8]).append("</ARTBEZ>");
                line.append("\n\t<BMENGE>").append(rech[9]).append("</BMENGE>");
                line.append("\n\t<ERBET>").append(rech[10]).append("</ERBET>");
                line.append("\n</RECH>");

                Util.writeFile("RECH-" + kubest.get(0)[0] + ".xml", line.toString());

                System.out.println("\n------Rechnung erstellt!--------\n");

                parseXML("RECH-" + kubest.get(0)[0] + ".xml", false);
                System.out.println("Elemente sind wohlgeformt");

                parseXML("RECH-" + kubest.get(0)[0] + ".xml", false);
                System.out.println("Elemente sind valide");
                return 0;
            } else {
                return 1;
            }
        } else {
            return -1;
        }

    }

    // Zeig alle Datensätze der Tabelle Artikel
    public static void anzeigenAllerArtikel(OracleConnection con) throws SQLException {

        String spalten[] = {"ARTNR", "ARTBEZ", "MGE", "PREIS", "STEU", "EDAT"};
        ArrayList<String[]> artikel;

        artikel = con.select("Artikel", spalten, null);
        ausgabe(spalten, artikel);
    }

    // Zeig alle Datensätze der Tabelle Lager
    public static void anzeigenAllerLager(OracleConnection con) throws SQLException {
        String spalten[] = {"LNR", "LORT", "LPLZ"};
        ArrayList<String[]> lager;

        lager = con.select("LAGER", spalten, null);
        ausgabe(spalten, lager);
    }

    // Zeig alle Datensätze der Tabelle Kunden
    public static void anzeigenAllerKunden(OracleConnection con) throws SQLException {
        String spalten[] = {"KNR", "KNAME", "PLZ", "ORT", "STRASSE"};
        ArrayList<String[]> kunde;

        kunde = con.select("KUNDE", spalten, null);
        ausgabe(spalten, kunde);
    }

    // Gibt alle Stammdaten eines Artikels und seiner Lagerbestände aus
    public static int stammdaten(OracleConnection con, String artikelnummer) throws SQLException {

        String spaltenArtikel[] = {"ARTNR", "ARTBEZ", "MGE", "PREIS", "STEU", "EDAT"},
                spaltenLagerbestand[] = {"BSTNR", "ARTNR", "LNR", "MENGE"},
                spaltenLager[] = {"LNR", "LORT", "LPLZ"},
                spaltenErgebnis[] = {"ARTNR", "ARTBEZ", "MGE", "PREIS", "STEU", "EDAT", "BSTNR", "LNR", "MENGE", "LORT", "LPLZ"},
                artnr, artbez, mge, preis, steu, edat, bstnr, lnr, menge, lort, lplz;
        HashSet<Integer> lagerNr = new HashSet<Integer>();
        ArrayList<String[]> artikel, lagerbestand, lager, listErgebnis = new ArrayList<String[]>();
        int s1 = 0;
        StringBuilder line;

        // Holen aller Artikel mit gegebener Artikelnummer
        artikel = con.select("Artikel", spaltenArtikel, "ARTNR = " + artikelnummer);

        if (artikel.size() > 0) {
            // Holen aller Lagerbestände mit gegebener Artikelnummer
            lagerbestand = con.select("LAGERBESTAND", spaltenLagerbestand, "ARTNR = " + artikelnummer);

            artnr = artikel.get(0)[0];
            artbez = artikel.get(0)[1];
            mge = artikel.get(0)[2];
            preis = artikel.get(0)[3];
            steu = artikel.get(0)[4];
            edat = artikel.get(0)[5];

            if (lagerbestand.size() > 0) {

                // sammeln der Lagernummer aller beteiligten Lager
                for (String arr[] : lagerbestand) {
                    lagerNr.add(Integer.parseInt(arr[2]));
                }

                // Aufbauen des select-Statements für die Tabelle Lager
                line = new StringBuilder();
                Integer tmpLnr[] = lagerNr.toArray(new Integer[lagerNr.size()]);
                line.append("(");
                for (int i = 0; i < lagerNr.size(); i++) {
                    if (i > 0) {
                        line.append(", ");
                    }
                    line.append(tmpLnr[i].toString());
                }
                line.append(")");

                // Holen der Lager
                lager = con.select("LAGER", spaltenLager, "LNR IN " + line.toString());

                for (String arrLB[] : lagerbestand) {
                    bstnr = arrLB[0];
                    lnr = arrLB[2];
                    menge = arrLB[3];

                    s1 += Integer.parseInt(arrLB[3]);

                    for (String arrL[] : lager) {
                        if (lnr.equals(arrL[0])) {
                            lort = arrL[1];
                            lplz = arrL[2];

                            // speichern der Ergebniszeilen
                            String tmpErg[] = {artnr, artbez, mge, preis, steu, edat, bstnr, lnr, menge, lort, lplz};
                            listErgebnis.add(tmpErg);
                        }
                    }
                }
            } else {
                // speichern der Ergebniszeilen falls kein Lagerbestand vorhanden
                String tmpErg[] = {artnr, artbez, mge, preis, steu, edat, null, null, null, null, null};
                listErgebnis.add(tmpErg);
            }

            // Direktes ausgeben des Ergebnises
            ausgabe(spaltenErgebnis, listErgebnis);
            System.out.println("Menge: " + s1 + "\n");
            return s1;

        } else {

            System.out.println("\nKein Artikel gefunden\n");
            return -1;

        }
    }

    // neuen Lagerbestand erfassen
    public static void erfassenLagerbestand(OracleConnection con, String bstnr, String artnr, String lnr, String menge) throws SQLException {
        String tabelle = "LAGERBESTAND", spalten[] = {"BSTNR", "ARTNR", "LNR", "MENGE"}, werte[] = {bstnr, artnr, lnr, menge};

        Util.insert(con, tabelle, spalten, werte);
    }

    // die Menge eines Lagerbestandes ändern
    public static void updateMenge(OracleConnection con, String bstnr, String menge) throws SQLException {
        String spalten[] = {"MENGE"},
                werte[] = {menge},
                where = "BSTNR = " + bstnr;

        Util.update(con, "LAGERBESTAND", spalten, werte, where);

    }

    // Hilfsfunktion für Ausgabe
    public static void ausgabe(String spalten[], ArrayList<String[]> arrList) {
        StringBuilder line;

        System.out.println();

        line = new StringBuilder();
        //Ausgabe der Spaltennamen
        for (String str : spalten) {
            line.append(str);
            System.out.printf("%15s|", str);

        }

        System.out.println();

        //Ausgabe des Tabellen Inhalts
        for (String[] arr : arrList) {

            line = new StringBuilder();
            for (String str : arr) {

                str = str.replace("00:00:00.0", "");
                System.out.printf("%15s ", str);

            }
            System.out.println(line.toString());
        }

        System.out.println();
    }

    public static int benrNext(OracleConnection con) throws SQLException {
        int ret = 0;
        ArrayList<String[]> kubest;
        String spalten[] = {"BENR"};
        kubest = con.select("KUBEST", spalten, null);

        for (String arr[] : kubest) {
            if (Integer.parseInt(arr[0]) > ret) {
                ret = Integer.parseInt(arr[0]);
            }
        }

        return ret + 1;
    }

    public static void parseXML(String file, boolean validate) throws ParserConfigurationException, SAXException, FileNotFoundException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(validate);
        XMLReader xmlReader = factory.newSAXParser().getXMLReader();
        xmlReader.setContentHandler(new DBContentHandler());
        xmlReader.setErrorHandler(new DBErrorHandler());

        InputSource is = new InputSource(new FileReader(file));
        is.setSystemId("rechnung.dtd");
        xmlReader.parse(is);
    }
}
