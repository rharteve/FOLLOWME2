import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Scanner;

// -----------------------------------------
// ISSUE met {} nog oplossen
// FOLLOWME2 compiled niet!
// -----------------------------------------

// Start simple...
public class FOLLOWME2 {

public static final String DATE_FORMAT_NOW = "dd-MM-yyyy HH:mm";

public static String now() {
Calendar cal = Calendar.getInstance();
SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
return sdf.format(cal.getTime());
}

   public static void main( String [] args ){


   // VARIABLEN
   int debug = 1;
   int sleepmin = 5;
   int sleepminnu = 0;
   String aandeel=""; 
   double HUIDIGEKOERS=0.00;
   double AFSTAND=0.00;
   boolean wegaandoor=true;



   // READ INPUT PARAMS ...
   sleepmin = Integer.parseInt(args[0]);
  // AFSTAND = Double.parseDouble(args[2]);
  // aandeel = args[0];



       //////////////////////////
       // Nu het echte werk  ////
       //////////////////////////

 

       ///////////////////////////////////////////
       /// HERE STARTS THE LOOP 
       ///////////////////////////////////////////
       while (wegaandoor){

         // READ CONFIG FILE IF ANY...
           String datafile="followme.lst";
           File g = null;
           FileReader p = null;
           BufferedReader leesme = null;
           int startarray=0;

           g = new File(datafile);

           try { leesme = new BufferedReader(new FileReader(g));
                 String myline="";
                 while (( myline= leesme.readLine()) !=null ){
                   if ( debug > 2 ) {
                      System.out.println("CONFIG FILE LINE: " + myline );
                   }


                   // Read AANDEEL en AFSTAND
                   aandeel=myline.substring(0,19).trim();
                   AFSTAND=Double.parseDouble(myline.substring(20));

                   if (debug > 1 ) {
                        System.out.printf("Aandeel = %s, AFSTAND=%f %n", aandeel, AFSTAND); 
                   }

                   // Haal huidige prijs op van website
                   double PREVKOERS=HUIDIGEKOERS;
                   HUIDIGEKOERS = -1.00;
                   String websiteURL = "https://www.behr.nl/index.php/fondsdetail/detail/" + aandeel;

                    try {
                      URL url = new URL(websiteURL);
                      Scanner sc = new Scanner(url.openStream());
                      // read from your scanner
                      String regel;

                      // Lets Scan all rows for @
                         while (sc.hasNext() == true ) {
                                regel = sc.next();

                                // Ophalen huidige koers
                                int B = regel.indexOf('@');
                                if ( B >= 0 ){
                                  // De volgende is de koers
                                  String KOERS=sc.next();
      //                            System.out.println("Koers " + aandeel + " "+  now() + " : " + KOERS);

                                  // Vertaal string naar double
                                  // Shit , ipv .
                                  KOERS = KOERS.replace(',', '.');
                                  HUIDIGEKOERS = Double.parseDouble(KOERS);
                                } // end-of-if
                          } // end-of-while-read-regels
                   sc.close();
               } // end-of-try
                  catch(IOException ex) {
                         HUIDIGEKOERS=-1.00;
                         if ( debug > 0 ) {
                             ex.printStackTrace();
                             System.out.println("ERROR: **** WEBSITE KOERS ONBEREIKBAAR " + aandeel);
                         } // end-of-debug
              } // end-of-catch
        

            ////// MAIN PRINT FUNCTION!!
            Double afstand = HUIDIGEKOERS - AFSTAND;
            Double perc = afstand/HUIDIGEKOERS*100;
      
            
             System.out.printf("FOLLOW %s ( with %d mins delays) afstand = %.2f %n", aandeel, sleepmin, AFSTAND);
            }

             System.out.printf( "Koers %s op %s : %.2f = afstand=%.2f (%.0f %%) %n", aandeel, now(), HUIDIGEKOERS, afstand, perc);

               System.out.println("");
           
               } // end-of-while-loop
               //Close file
               leesme.close();

            } // End of try
            catch (IOException ex) {
                System.out.println("OEPS GEEN CONF FILE");
                System.out.println("start all over.....");
                //ex.printStackTrace();
            } // end-of-catch



          // even wachten om de website niet te overbelasten...
           // Beetje wachtpauze
           if (wegaandoor){
       
              // dynamische sleep minutes
              // Bepaal of beurs open is
              int uur = Integer.parseInt(now().substring(11,13) );
              sleepminnu=sleepmin ; 
              if ( uur > 18 ) {
                  sleepminnu=60;
              }  
              if ( uur < 8 ) {
                  sleepminnu=60;
              } 
          


        if ( sleepminnu == 60 ){
            System.out.println("Beurs is nu gesloten.... (next update in 60 mins)");
        }
        ////// MAIN PRINT FUNCTION!!

               try { Thread.sleep(1000*60*sleepminnu); 
                   } 
               catch ( InterruptedException e ) { 
                   	System.out.println( " Thread Interrupted ");
               }  //end-of-catch
          //  } // end-of-if

      } // Einde Loop wegaandoor


  } // end of main
} // end of CLASS
