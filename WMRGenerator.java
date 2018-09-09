import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;

public class WMRGenerator {
    public static void main(String[] args) {
        int mode = 0;
        long seed = new Random().nextLong();
        boolean printSepRot = false;
        boolean printGTypes = false;
        boolean printFullRot = false;
        boolean printLength = false;
        String outPath = "";
        String inPath = "";
        String forHelp = "For help, type: java WMRGenerator -h";
        //handle arguments
        if (args.length == 0) {// help
            System.out.println(forHelp);
        } else if (args[0].equals("--help") || args[0].equals("-h")) {// help
            System.out.println("WoP Map Rotation Generator 1.0\n" + 
                    "\n" + 
                    "Usage: java WMRGenerator [OPTION]...\n" + 
                    "\n" + 
                    "-a, --append    FILE        set in- and output file to FILE*\n" + 
                    "-f, --full                  print out the full resulting map sequence, from the beginning\n" + 
                    "                            to the end of the longest rotation over all game types\n" + 
                    "-h, --help                  give this help\n" + 
                    "-i, --in        FILE        set input file to FILE*\n" + 
                    "-k, --seedkey   SEED        parse a random seed from SEED\n" + 
                    "-l, --length                print out the length of the sequence until the first map \n" + 
                    "                            appears for the second time\n" + 
                    "-m, --mode      MODE        set the mode for generating rotation:\n" + 
                    "                            <0 - use all available maps, but only for one game type each\n" + 
                    "                             0 - use all available maps for each game type (default)\n" + 
                    "                            >0 - set the fix length for the rotation of each game type\n" + 
                    "                                 resulting in the same sequence if the rotation starts over\n" + 
                    "-o, --out       FILE        set output file to FILE*\n" + 
                    "-s, --separate              print out the rotation separated by game types\n" + 
                    "-t, --types                 print out the order of game types\n" + 
                    "\n" + 
                    "*If an input file is set WMRGenerator will read the input, append the rotation and write to\n" + 
                    " the output file. Therefore setting input and output file separately to the same file is the \n" + 
                    " same as using --append. If no output file is set WMRGenerator will neither read nor write\n" + 
                    " any file.");
        } else {
            for(int i = 0; i< args.length;i++) {
                if (      args[i].equals("-m") || args[i].equals("--mode")) { // mode
                    try{
                        mode = Integer.parseInt(args[i+1]);
                        i++;
                     }catch(NumberFormatException e){
                        System.out.println("Wrong mode format: Cannot parse int from input \"" + args[i+1] + "\"" );
                        return;
                     }
                    i++;
                } else if(args[i].equals("-s") || args[i].equals("--separate")) { // printSepRot
                    printSepRot = true;
                } else if(args[i].equals("-t") || args[i].equals("--types")) { // printGTypes
                    printGTypes = true;
                } else if(args[i].equals("-f") || args[i].equals("--full")) { // printFullRot
                    printFullRot = true;
                } else if(args[i].equals("-l") || args[i].equals("--length")) { // printLength
                    printLength = true;
                } else if(args[i].equals("-i") || args[i].equals("--in")) { // input File
                    inPath = args[i+1];
                    i++;
                } else if(args[i].equals("-o") || args[i].equals("--out")) { // output File
                    outPath = args[i+1];
                    i++;
                } else if(args[i].equals("-a") || args[i].equals("--append")) { // inoutput File
                    inPath = args[i+1];
                    outPath = args[i+1];
                    i++;
                } else if(args[i].startsWith("-k") || args[i].startsWith("--seedkey")){ //seed
                    try{
                        seed = Long.parseLong(args[i+1]);
                        i++;
                     }catch(NumberFormatException e){
                        System.out.println("Wrong random seed format: Cannot parse long from input \"" + args[i+1] + "\"" );
                        return;
                     }
                } else {
                    System.out.println(forHelp);
                }
            }
            //create generator
            Generator generator = new Generator();
            generator.setSeed(seed);
            generator.shuffleGameTypes();
            if (printLength) {
                System.out.println("");
                System.out.println("Length of rotation without doubles:");
                System.out.println("");
                System.out.println(generator.createRandomSequence(mode));
            } else {
                generator.createRandomSequence(mode);
            }
            if (printSepRot) {
                System.out.println("");
                System.out.println("Seperate game type rotations:");
                generator.printSeparateRotations();
            }
            if (printGTypes) {
                System.out.println("");
                System.out.println("Order of game types:");
                generator.printGameTypes();
            }
            if (printFullRot) {
                System.out.println("");
                System.out.println("Full rotation:");
                generator.printFullRotation();
            }
            //write output file
            if (!outPath.equals("")) {
                if (!inPath.equals("")) {
                    if (!inPath.equals(outPath)) {
                        // read and write in different files
                        String out = "";
                        try {
                            out = readFile(inPath);
                        } catch (IOException e) {
                            System.err.println("Error: Could not read file: " + inPath);
                        }
                        out += generator.generateOutput();
                        try {
                            File file = new File(outPath);
                            file.createNewFile();
                            writeStringToFile(out, outPath);
                        } catch (IOException e) {
                            System.err.println("Error: Could not write file: " + outPath);
                        }
                    } else {
                        // append to file
                        try {
                            File file = new File(outPath);
                            file.createNewFile();
                            appendStringToFile(generator.generateOutput(), outPath);
                        } catch (IOException e) {
                            System.err.println("Error: Could not append to file: " + outPath);
                        }
                    }
                } else {
                    // only write
                    try {
                        File file = new File(outPath);
                        file.createNewFile();
                        writeStringToFile(generator.generateOutput(), outPath);
                    } catch (IOException e) {
                        System.err.println("Error: Could not write file: " + outPath);
                    }
                }
            }
        }
    }
    
    private static String readFile(String inPath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(inPath)));
    }
    
    public static void writeStringToFile(String text, String outPath) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(outPath, false));
        writer.append(text);
        writer.close();
    }
    public static void appendStringToFile(String text, String inOutPath) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(inOutPath,true));
        writer.append("\n");
        writer.append(text);
        writer.close();
    }
}

class WMap {

    private String nName; //name FFA and TDM gametypes
    private String sName; //name for SyC and TSyC gametypes
    private String lName; //name for LPS gametype
    private String bName; //name for BB gametype
    private String cName; //name for CTL gametype
    private int rating = 0;   //used for choosing the best map which is already in current map sequence
    

    public String getnName() {
        return nName;
    }
    public void setnName(String name) {
        this.nName = name;
    }
    public String getsName() {
        return sName;
    }
    public void setsName(String name) {
        this.sName = name;
    }
    public String getlName() {
        return lName;
    }
    public void setlName(String lName) {
        this.lName = lName;
    }
    public String getbName() {
        return bName;
    }
    public void setbName(String bName) {
        this.bName = bName;
    }
    public String getcName() {
        return cName;
    }
    public void setcName(String cName) {
        this.cName = cName;
    }
    public int getRating() {
        return rating;
    }
    public void setRating(int rating) {
        this.rating = rating;
    }
}

class Generator {
    
    //maps for BB gametype
    private String[] bbMaps = {
            "wop_anteroombb",
            "wop_backyardbb",
            "wop_bathbb",
            "wop_cabinbb",
            "wop_dinerbb",
            "wop_huettebb",
            "wop_jailbb",
            "wop_padatticbb",
            "wop_padcrashbb",
            "wop_padgardenbb",
            "wop_padkitchenbb",
            "wop_padlibrarybb",
            "wop_padshipbb",
            "wop_padshopbb",
            "wop_trashmapbb"
    };
    
    //maps for FFA and TDM gametypes
    private String[]  maps = {
            "wop_airplane",
            "wop_anteroom",
            "wop_backyard",
            "wop_bath",
            "wop_cabin",
            "wop_diner",
            "wop_huette",
            "wop_jail",
            "wop_padattic",
            "wop_padcrash",
            "wop_padcrash_dm17",
            "wop_padgarden",
            "wop_padkitchen",
            "wop_padlibrary",
            "wop_padship",
            "wop_padshop"
            //,"wop_trashmap"
    };

    
    //maps for SyC and TSyC gametypes
    private String[]  sycMaps = {
            "wop_airplane",
            "wop_anteroom",
            "wop_backyard",
            "wop_bath",
            "wop_cabin",
            "wop_diner",
            "wop_huette",
            "wop_jail",
            "wop_padattic",
            "wop_padgarden",
            "wop_padkitchen",
            "wop_padlibrary",
            "wop_padship",
            "wop_padshop"
            //,"wop_trashmap"
    };
    
    //maps for CTL gametype
    private String[]  ctlMaps = {
            "wop_colorstagectl",
            "wop_fridgectl",
            "wop_journeyctl",
            "wop_padasiactl",
            "wop_padbasectl",
            "wop_padboxctl",
            "wop_padcloisterctl",
            "wop_padcrashctl",
            "wop_westernctl"
    };
    
    //maps for LPS gametype
    private String[]  lpsMaps = {
            "wop_airplane",
            "wop_anteroom",
            "wop_backyard",
            "wop_bath",
            "wop_cabin",
            "wop_diner",
            "wop_huette",
            "wop_jail",
            "wop_padatticlps",
            "wop_padcrash",
            "wop_padcrash_dm17",
            "wop_padgarden_night",
            "wop_padkitchenlps",
            "wop_padlibrarylps",
            "wop_padship",
            "wop_padshop"
            //,"wop_trashmap"
    };
    
    //map collections
    private HashMap<String, WMap> mapSet;
    private LinkedList<String> gameTypes;
    
    private LinkedList<WMap> sequence;    // full map sequence
    private LinkedList<WMap> mRotation;   // rotation for FFA and TDM gametypes 
    private LinkedList<WMap> msRotation;   // rotation for SyC and TSyC gametypes 
    private LinkedList<WMap> mlRotation;  // rotation for LPS gametype 
    private LinkedList<WMap> mbRotation;  // rotation for BB gametype 
    private LinkedList<WMap> mcRotation;  // rotation for CTL gametype 
    private HashSet<WMap> mUnused;   // unused for FFA and TDM gametypes 
    private HashSet<WMap> msUnused;   // unused for SyC and TSyC gametypes 
    private HashSet<WMap> mlUnused;   // unused for LPS gametype 
    private HashSet<WMap> mbUnused;   // unused for for BB gametype 
    private HashSet<WMap> mcUnused;   // unused for for CTL gametype 
    
    //random 
    private Random rand;
    
    public Generator() {
        setupGameTypes();
        setupMapSet();
    }
    
    private void setupGameTypes() {
        gameTypes = new LinkedList<>();
        gameTypes.add("ffa");
        gameTypes.add("tffa");
        gameTypes.add("syc");
        gameTypes.add("tsyc");
        gameTypes.add("lps");
        gameTypes.add("bb");
        gameTypes.add("ctl");
    }
    
    public void shuffleGameTypes() {
        if (rand == null) {
            Collections.shuffle(gameTypes);
        } else {
            Collections.shuffle(gameTypes, rand);
        }
    }
    
    public void setSeed(long seed) {
        rand = new Random(seed);
    }
    
    private void setupMapSet() {
        mapSet = new HashMap<>();
        mUnused = new HashSet<>();
        msUnused = new HashSet<>();
        mbUnused = new HashSet<>();
        mlUnused = new HashSet<>();
        mcUnused = new HashSet<>();
        String n;
        //start with maps for FFA and TDM gametypes
        for (String mName: maps) {
            n = cutSuffix(mName);
            // add Map instance to mapSet if not already present
            if (!mapSet.containsKey(n)) {
                mapSet.put(n, new WMap());
            }
            // set gametype specific map name
            mapSet.get(n).setnName(mName);
            mUnused.add(mapSet.get(n));
        }
        //maps for SyC and TSyC gametypes
        for (String mName: sycMaps) {
            n = cutSuffix(mName);
            // add Map instance to mapSet if not already present
            if (!mapSet.containsKey(n)) {
                mapSet.put(n, new WMap());
            }
            // set gametype specific map name
            mapSet.get(n).setsName(mName);
            msUnused.add(mapSet.get(n));
        }
        //maps for LPS gametype
        for (String mName: lpsMaps) {
            n = cutSuffix(mName);
            // add Map instance to mapSet if not already present
            if (!mapSet.containsKey(n)) {
                mapSet.put(n, new WMap());
            }
            // set gametype specific map name
            mapSet.get(n).setlName(mName);
            mlUnused.add(mapSet.get(n));
        }
        //maps for BB gametype
        for (String mName: bbMaps) {
            n = cutSuffix(mName);
            // add Map instance to mapSet if not already present
            if (!mapSet.containsKey(n)) {
                mapSet.put(n, new WMap());
            }
            // set gametype specific map name
            mapSet.get(n).setbName(mName);
            mbUnused.add(mapSet.get(n));
        }
        //maps for CTL gametype
        for (String mName: ctlMaps) {
            n = cutSuffix(mName);
            // add Map instance to mapSet if not already present
            if (!mapSet.containsKey(n)) {
                mapSet.put(n, new WMap());
            }
            // set gametype specific map name
            mapSet.get(n).setcName(mName);
            mcUnused.add(mapSet.get(n));
        }
    }
    
    /**
     * Creates a random sequence of maps for the given gametype order containing maps only once
     * and populates the gametype specific rotation lists.
     * @param count number of maps per gametype in the rotation after the rotation will start again.
     * If count = 0 all maps appear in the rotation with all possible gametypes.
     * If count < 0 all maps are in the rotation but appear only for one distinct gametype.
     * @return Length of the sequence.
     * If the return value for example is 23, then the 24th map will already be one of the first 23.
     */
    public int createRandomSequence(int count) {
        int i = 0;
        mRotation = new LinkedList<>();
        msRotation = new LinkedList<>();
        mbRotation = new LinkedList<>();
        mlRotation = new LinkedList<>();
        mcRotation = new LinkedList<>();
        sequence = new LinkedList<>();
        boolean contSeq = true;
        int mapNr = 0;
        LinkedList<String> mapNames;
        while (contSeq) {
            i = 0;
            for (; i < gameTypes.size(); i++) {
                // shuffle the names of all maps which have not been used yet
                mapNames = asShuffledList(mapSet.keySet());
                // take the first map which can be played with the next gametype
                contSeq = pickFirstForGametype(mapNames, gameTypes.get(i));
                if (!contSeq) {
                    // there is no suitable map left, abort
                    break;
                }
                mapNr++;
            }
            //continue the sequence if the upper loop has not been aborted and the sequence is not finished yet
            contSeq = contSeq && ((mapNr < count*gameTypes.size()) || (count < 1 && !mapSet.isEmpty()));
        }
        
        //finish the rotation if the loop has been aborted
        if (sequence.size() < count*gameTypes.size()) {
            // continue as before but if there are no new maps left,
            // try to use a map which was at least not used for this gametype
            while (sequence.size() < count*gameTypes.size()) {
                for (; i < gameTypes.size(); i++) {
                    // shuffle the names of all maps which have not been used yet
                    mapNames = asShuffledList(mapSet.keySet());
                    // take the first map which can be played with the next gametype
                    if(!pickFirstForGametype(mapNames, gameTypes.get(i))) {
                        bestEffortNewMap(i, count*gameTypes.size(), false);
                    }
                }
                i = 0; //reset i after the first for-loop to resume work where it was interrupted
            }
        } else if (count == 0 && !mapSet.isEmpty()) {
            // continue as before but if there are no new maps left,
            // either use a map which was at least not used for this gametype or leave the map out
            while (!mUnused.isEmpty()||
                    !msUnused.isEmpty()||
                    !mlUnused.isEmpty()||
                    !mbUnused.isEmpty()||
                    !mcUnused.isEmpty()) {
                for (; i < gameTypes.size(); i++) {
                    // shuffle the names of all maps which have not been used yet
                    mapNames = asShuffledList(mapSet.keySet());
                    // take the first map which can be played with the next gametype
                    if(!pickFirstForGametype(mapNames, gameTypes.get(i))) {
                        bestEffortNewMap(i, -1,true);
                    }
                }
                i = 0; //reset i after the first for-loop to resume work where it was interrupted
            }
        } else if (count < 0 && !mapSet.isEmpty()) {
            // put all maps which have not been chosen yet in the rotation
            // shuffle the names of all remaining maps
            mapNames = asShuffledList(mapSet.keySet());
            int minLength = mRotation.size();
            //do not try to fill the list if there are no maps left for this gametype
            boolean fillM = true;
            boolean fillMs = true;
            boolean fillMb = true;
            boolean fillMl = true;
            boolean fillMc = true;
            while (!mapSet.isEmpty()) {
                //determine new minLength
                if(fillM)
                    minLength = mRotation.size()/2;
                if(fillMs)
                    minLength = Math.min(minLength, msRotation.size()/2);
                if(fillMb)
                    minLength = Math.min(minLength, mbRotation.size());
                if(fillMl)
                    minLength = Math.min(minLength, mlRotation.size());
                if(fillMc)
                    minLength = Math.min(minLength, mcRotation.size());
                
                //fill rotations
                if(fillM && mRotation.size()/2 == minLength)
                    fillM = pickFirstForGametype(mapNames, "ffa")&&
                            pickFirstForGametype(mapNames, "tffa");
                if(fillMs && msRotation.size()/2 == minLength)
                    fillMs = pickFirstForGametype(mapNames, "syc")&&
                            pickFirstForGametype(mapNames, "tsyc");
                if(fillMb && mbRotation.size() == minLength) 
                    fillMb = pickFirstForGametype(mapNames, "bb");
                if(fillMl && mlRotation.size() == minLength) 
                    fillMl = pickFirstForGametype(mapNames, "lps");
                if(fillMc && mcRotation.size() == minLength) 
                    fillMc = pickFirstForGametype(mapNames, "ctl");
                
                //increase minLength
                minLength++;
            }
        }
        return mapNr;
    }
    
    /**
     * Rates all maps by estimating the distance from the current free position to the next
     * occurrence of the map in the final map sequence and returns the Map with the highest result.
     * @param maps
     * @param targetSeqLength
     * @return best rated {@link Map}
     */
    private WMap getBestRatedMap(Collection<WMap> maps, int targetSeqLength) {
        for (WMap map: maps) {
            map.setRating(0);
            for (int distance = 1; map.getRating() == 0 && distance < targetSeqLength; distance++) {
                int forwards = distance - targetSeqLength+sequence.size();
                int backwards = sequence.size()-distance;
                if (forwards >= 0 && map.equals(sequence.get(forwards))) 
                    map.setRating(distance);
                if (backwards >= 0 && map.equals(sequence.get(backwards)))
                    map.setRating(distance);
                
            }
            if(map.getRating()==0)
                map.setRating(targetSeqLength);
        }
        int best = 0;
        WMap out = null;
        for (WMap map: maps) {
            if(best < map.getRating()) {
                best = map.getRating();
                out = map;
            }
        }
        return out;
    }
    
    /**
     * Chooses a map which was at least not used for this gametype 
     * or has the largest distance in rotation
     * @param gTypeIndex index of the current gametype in gameTypes
     * @param targetSeqLength length of the finished sequence. Uses own estimation if set to -1.
     * @param leaveOut do nothing if there are no unused maps left for this gametype
     */
    private void bestEffortNewMap(int gTypeIndex, int targetSeqLength, boolean leaveOut) {
        WMap map;
        switch (gameTypes.get(gTypeIndex)) {
        case "ffa":
        case "tffa":
            if (targetSeqLength == -1) //estimate length
                targetSeqLength = gameTypes.size()* maps.length;
            mUnused.remove(null);
            if (!mUnused.isEmpty() || !leaveOut) {
                if(!mUnused.isEmpty()) {
                    map = getBestRatedMap(mUnused, targetSeqLength);
                } else {// no unused map left
                    map = mRotation.get(mRotation.size()-mRotation.size()/2);
                }
                sequence.add(map);
                mRotation.add(map);
                mUnused.remove(map);
            }
            break;
        case "syc":
        case "tsyc":
            if (targetSeqLength == -1) //estimate length
                targetSeqLength = gameTypes.size()* sycMaps.length;
            msUnused.remove(null);
            if (!msUnused.isEmpty() || !leaveOut) {
                if(!msUnused.isEmpty()) {
                    map = getBestRatedMap(msUnused, targetSeqLength);
                } else {// no unused map left
                    map = msRotation.get(msRotation.size()-msRotation.size()/2);
                }
                sequence.add(map);
                msRotation.add(map);
                msUnused.remove(map);
            }
            break;
        case "bb":
            if (targetSeqLength == -1) //estimate length
                targetSeqLength = gameTypes.size()* bbMaps.length;
            mbUnused.remove(null);
            if (!mbUnused.isEmpty() || !leaveOut) {
                if(!mbUnused.isEmpty()) {
                    map = getBestRatedMap(mbUnused, targetSeqLength);
                } else {// no unused map left
                    map = mbRotation.get(mbRotation.size()-mbRotation.size()/2);
                }
                sequence.add(map);
                mbRotation.add(map);
                mbUnused.remove(map);
            }
            break;
        case "lps":
            if (targetSeqLength == -1) //estimate length
                targetSeqLength = gameTypes.size()* lpsMaps.length;
            mlUnused.remove(null);
            if (!mlUnused.isEmpty() || !leaveOut) {
                if(!mlUnused.isEmpty()) {
                    map = getBestRatedMap(mlUnused, targetSeqLength);
                } else {// no unused map left
                    map = mlRotation.get(mlRotation.size()-mlRotation.size()/2);
                }
                sequence.add(map);
                mlRotation.add(map);
                mlUnused.remove(map);
            }
            break;
        case "ctl":
            if (targetSeqLength == -1) //estimate length
                targetSeqLength = gameTypes.size()* ctlMaps.length;
            mcUnused.remove(null);
            if (!mcUnused.isEmpty() || !leaveOut) {
                if(!mcUnused.isEmpty()) {
                    map = getBestRatedMap(mcUnused, targetSeqLength);
                } else {// no unused map left
                    map = mcRotation.get(mcRotation.size()-mcRotation.size()/2);
                }
                sequence.add(map);
                mcRotation.add(map);
                mcUnused.remove(map);
            }
            break;
        default:
            System.out.println("Warning: Strange gametype detected!");
            break;
        }
    }
    
    /**
     * Chooses the first suitable map as next map for rotation
     * @param mList list of maps to choose from
     * @param gameType for the next match
     * @return false if no fitting map found, true otherwise
     */
    private boolean pickFirstForGametype(LinkedList<String> mList, String gameType) {
        switch (gameType) {
        case "ffa":
        case "tffa":
            for(String m: mList) {
                WMap map = mapSet.get(m);
                if (map != null && map.getnName() != null) {
                    sequence.add(map);
                    mRotation.add(map);
                    mapSet.remove(m);
                    mUnused.remove(map);
                    return true;
                }
            }
            return false;
        case "syc":
        case "tsyc":
            for(String m: mList) {
                WMap map = mapSet.get(m);
                if (map != null && map.getsName() != null) {
                    sequence.add(map);
                    msRotation.add(map);
                    mapSet.remove(m);
                    msUnused.remove(map);
                    return true;
                }
            }
            return false;
        case "bb":
            for(String m: mList) {
                WMap map = mapSet.get(m);
                if (map != null && map.getbName() != null) {
                    sequence.add(map);
                    mbRotation.add(map);
                    mapSet.remove(m);
                    mbUnused.remove(map);
                    return true;
                }
            }
            return false;
        case "lps":
            for(String m: mList) {
                WMap map = mapSet.get(m);
                if (map != null && map.getlName() != null) {
                    sequence.add(map);
                    mlRotation.add(map);
                    mapSet.remove(m);
                    mlUnused.remove(map);
                    return true;
                }
            }
            return false;
        case "ctl":
            for(String m: mList) {
                WMap map = mapSet.get(m);
                if (map != null && map.getcName() != null) {
                    sequence.add(map);
                    mcRotation.add(map);
                    mapSet.remove(m);
                    mcUnused.remove(map);
                    return true;
                }
            }
            return false;
        default:
            System.out.println("Warning: Strange gametype detected!");
            return false;
        }
    }
    
    /**
     * Returns a random String of the given set
     * @param set all Strings
     * @return random String
     */
    private LinkedList<String> asShuffledList(Collection<String> set) {
        LinkedList<String> list = new LinkedList<>(set);
        if (rand == null) {
            Collections.shuffle(list);
        } else {
            Collections.shuffle(list,rand);
        }
        return list;
    }
    
    /**
     * Removes gametype suffix from a map name
     * @param s name
     * @return name without suffix
     */
    private String cutSuffix(String s) {
        String n = s;
        // cut suffix
        if (s.endsWith("ctl")) {
            n = s.replace("ctl", "");
        } else if (s.endsWith("bb")) {
            n = s.replace("bb", "");
        } else if (s.endsWith("lps")) {
            n = s.replace("lps", "");
        }  else if (s.endsWith("_night")) { // wop_padgarden_night as LPS version of wop_padgarden
            n = s.replace("_night", "");
        } 
        return n;
    }
    
    public void printGameTypes() {
        System.out.println("");
        for(int i = 0; i < gameTypes.size(); i++) {
            System.out.println(gameTypes.get(i));
        }
    }
    public void printFullRotation() {
        int iterations = Math.max(mRotation.size()/2,  Math.max(msRotation.size()/2,
                Math.max(mbRotation.size(), Math.max(mlRotation.size(), mcRotation.size()))));
        for(int i = 0; i < iterations; i++) {
            System.out.println("");
            int mOffset = 0;
            int msOffset = 0;
            for (String gt: gameTypes) {
                switch (gt) {
                case "ffa":
                case "tffa":
                    System.out.println(mRotation.get((i*2+mOffset)%mRotation.size()).getnName());
                    mOffset++;
                    break;
                case "syc":
                case "tsyc":
                    System.out.println(msRotation.get((i*2+msOffset)%msRotation.size()).getsName());
                    msOffset++;
                    break;
                case "bb":
                    System.out.println(mbRotation.get(i%mbRotation.size()).getbName());
                    break;
                case "lps":
                    System.out.println(mlRotation.get(i%mlRotation.size()).getlName());
                    break;
                case "ctl":
                    System.out.println(mcRotation.get(i%mcRotation.size()).getcName());
                    break;
                default:
                    System.out.println("Warning: Strange gametype detected!");
                    break;
                }
            }
        }
    }
    public void printSeparateRotations() {
        System.out.println("");
        System.out.println("FFA and TDM:");
        for(WMap m: mRotation)
            System.out.println("    " + m.getnName());

        System.out.println("");
        System.out.println("SyC and TSyC:");
        for(WMap m: msRotation)
            System.out.println("    " + m.getsName());

        System.out.println("");
        System.out.println("BB:");
        for(WMap m: mbRotation)
            System.out.println("    " + m.getbName());

        System.out.println("");
        System.out.println("LPS:");
        for(WMap m: mlRotation)
            System.out.println("    " + m.getlName());

        System.out.println("");
        System.out.println("CTL:");
        for(WMap m: mcRotation)
            System.out.println("    " + m.getcName());
    }
    
    private String createNRotation() {
        String out = "// rotation for FFA and TDM gametypes\n";
        for (int i = 0; i < mRotation.size();i++) {
            out += "set m"+ i + " \"map ";
            out += mRotation.get(i).getnName();
            if (i+1 < mRotation.size()) {
                out += "; set nextnmap vstr m" + (i+1) + "\"\n";
            } else {
                out += "; set nextnmap vstr m0\"\n";
            }
        }
        out += "\n";
        return out;
    }
    private String createSRotation() {
        String out = "// rotation for SyC and TSyC gametypes\n";
        for (int i = 0; i < msRotation.size();i++) {
            out += "set ms"+ i + " \"map ";
            out += msRotation.get(i).getsName();
            if (i+1 < msRotation.size()) {
                out += "; set nextsmap vstr ms" + (i+1) + "\"\n";
            } else {
                out += "; set nextsmap vstr ms0\"\n";
            }
        }
        out += "\n";
        return out;
    }
    private String createLRotation() {
        String out = "// rotation for LPS gametype\n";
        for (int i = 0; i < mlRotation.size();i++) {
            out += "set ml"+ i + " \"map ";
            out += mlRotation.get(i).getlName();
            if (i+1 < mlRotation.size()) {
                out += "; set nextnmap vstr ml" + (i+1) + "\"\n";
            } else {
                out += "; set nextnmap vstr ml0\"\n";
            }
        }
        out += "\n";
        return out;
    }
    private String createBRotation() {
        String out = "// rotation for BB gametype\n";
        for (int i = 0; i < mbRotation.size();i++) {
            out += "set mb"+ i + " \"map ";
            out += mbRotation.get(i).getbName();
            if (i+1 < mbRotation.size()) {
                out += "; set nextnmap vstr mb" + (i+1) + "\"\n";
            } else {
                out += "; set nextnmap vstr mb0\"\n";
            }
        }
        out += "\n";
        return out;
    }
    private String createCRotation() {
        String out = "// rotation for CTL gametype\n";
        for (int i = 0; i < mcRotation.size();i++) {
            out += "set mc"+ i + " \"map ";
            out += mcRotation.get(i).getcName();
            if (i+1 < mcRotation.size()) {
                out += "; set nextnmap vstr mc" + (i+1) + "\"\n";
            } else {
                out += "; set nextnmap vstr mc0\"\n";
            }
        }
        out += "\n";
        return out;
    }
    private String createGTypeRotation() {
        String out = "//*** Gametype Rotation ***\n";
        for (int i = 0; i < gameTypes.size();i++) {
            out += "set g" + i +  " \"set nextnbmap vstr next";
            switch (gameTypes.get(i)) {
            case "ffa":
                out += "nmap; pointlimit 40; timelimit 15; g_gametype 0; set nextgtype vstr g";
                break;
            case "tffa":
                out += "nmap; pointlimit 80; timelimit 15; g_gametype 5; set nextgtype vstr g";
                break;
            case "syc":
                out += "smap; pointlimit 200; timelimit 15; g_gametype 3; set nextgtype vstr g";
                break;
            case "tsyc":
                out += "smap; pointlimit 300; timelimit 15; g_gametype 7; set nextgtype vstr g";
                break;
            case "bb":
                out += "bmap; pointlimit 400; timelimit 15; g_gametype 8; set nextgtype vstr g";
                break;
            case "lps":
                out += "lmap; timelimit 15; g_gametype 4; set nextgtype vstr g";
                break;
            case "ctl":
                out += "cmap; pointlimit 10; timelimit 15; g_gametype 6; set nextgtype vstr g";
                break;
            default:
                System.out.println("Warning: Strange gametype detected!");
                break;
            }
            //next gameTypeIndex
            if (i+1 < gameTypes.size()) {
                out += (i+1) + "\" //";
            } else {
                out += "0\" // ";
            }
            //gameType comment
            switch (gameTypes.get(i)) {
            case "ffa":
                out += "FFA\n";
                break;
            case "tffa":
                out += "TDM\n";
                break;
            case "syc":
                out += "SyC\n";
                break;
            case "tsyc":
                out += "TSyC\n";
                break;
            case "bb":
                out += "BB\n";
                break;
            case "lps":
                out += "LPS\n";
                break;
            case "ctl":
                out += "CtL\n";
                break;
            default:
                System.out.println("Warning: Strange gametype detected!");
                break;
            }
        }
        out += "\n";
        return out;
    }
    
    public String generateOutput() {
       String out = "//*** Map Rotation ***\n";
       
       // FFA and TDM
       out += createNRotation();
       
       // SyC and TSyC
       out += createSRotation();
       
       // LPS
       out += createLRotation();
       
       // BB
       out += createBRotation();
       
       // CTL
       out += createCRotation();
       
       //Gametype Rotation
       out += createGTypeRotation();
       
       // nextMap init
       out += "//*** modifying nextmap ***\n" + 
                "set nextmap \"vstr execnextmap\"\n" + 
                "set execnextmap \"vstr nextgtype; vstr nextnbmap; set nextmap vstr execnextmap\"\n" + 
                "\n" + 
                "//*** initialize the starting values\n" + 
                "set nextnmap \"vstr m0\"\n" + 
                "set nextsmap \"vstr ms0\"\n" + 
                "set nextbmap \"vstr mb0\"\n" + 
                "set nextlmap \"vstr ml0\"\n" + 
                "set nextcmap \"vstr mc0\"\n" + 
                "set nextnbmap \"vstr nextnmap\"\n" + 
                "\n" + 
                "vstr g" + (gameTypes.size()-1)
                + "\n" + 
                "vstr nextmap";
       
       return out;
    }
}