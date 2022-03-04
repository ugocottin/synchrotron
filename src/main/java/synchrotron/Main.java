package synchrotron;

import org.apache.commons.cli.*;
import sun.misc.Signal;
import synchrotron.synchronizer.Synchronizer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;


public class Main {

    public static void main(String[] args) throws Exception {
        final MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");

        int timerValue = 5000;

        Options options = new Options();
        options.addOption("p1", true, "Chemin d'accès au premier dossier");
        options.addOption("p2", true, "Chemin d'accès au deuxième dossier");
        options.addOption("t", true, "Update timer (ms)");
        CommandLineParser parser = new BasicParser();
        HelpFormatter formatter = new HelpFormatter();
        String param1 = "";
        String param2 = "";
        try {
            CommandLine line = parser.parse(options, args);
            if (line.hasOption("p1"))
                param1 = line.getOptionValue("p1");
            else {
                formatter.printHelp("Synchrotron", options);
                System.exit(0);
            }
            if (line.hasOption("p2"))
                param2 = line.getOptionValue("p2");
            else {
                formatter.printHelp("Synchrotron", options);
                System.exit(0);
            }
            if (line.hasOption("t")) {
                boolean isNumeric =  line.getOptionValue("t").matches("[+-]?\\d*(\\.\\d+)?");
                if(isNumeric)
                    timerValue = Integer.parseInt(line.getOptionValue("t"));
                else {
                    System.out.println("Le timer n'est pas une valeur numérique");
                    formatter.printHelp("Synchrotron", options);
                    System.exit(0);
                }
            }

        }catch (ParseException e) {
            formatter.printHelp("Synchrotron", options);
            System.exit(0);
        }
        System.out.println("Path1 : "+param1+", Path2 : "+param2+", timer : "+timerValue );

        //Path firstPath = Paths.get("C:/", "tmp", "public");
        //Path secondPath = Paths.get("C:/", "tmp", "public_copy");
        Path firstPath = Paths.get(param1);
        Path secondPath = Paths.get(param2);
        if(!firstPath.toFile().exists() || !firstPath.toFile().isDirectory()){
            System.out.println("Le chemin vers le premier dossier n'existe pas");
            System.exit(0);
        }
        if(!secondPath.toFile().exists() || !secondPath.toFile().isDirectory()){
            System.out.println("Le chemin vers le deuxième dossier n'existe pas");
            System.exit(0);
        }

        final Synchronizer synchronizer = new Synchronizer(messageDigest);

        Signal.handle(new Signal("INT"), signal -> synchronizer.stop());

        synchronizer.synchronize(firstPath, secondPath, timerValue);

        try {
            synchronizer.waitForExit();
        } catch (InterruptedException exception) {
            System.err.println(exception.getMessage());
        } finally {
            synchronizer.stop();
        }

        System.exit(0);
    }
}
