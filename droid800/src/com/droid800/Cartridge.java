package com.droid800;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import java.security.MessageDigest;

import java.util.HashMap;

/**
 * This class attempts to make cart file from a rom
 *
 *   +----+-----------+------+---------------------------------------------------+
 *   | Id |  Machine  | Size | Name                                              |
 *   |----+-----------+------+---------------------------------------------------+
 *   |  1 | 800/XL/XE |    8 | Standard 8 KB cartridge                           |
 *   |  2 | 800/XL/XE |   16 | Standard 16 KB cartridge                          |
 *   |  3 | 800/XL/XE |   16 | OSS '034M' 16 KB cartridge                        |
 *   |  4 |   5200    |   32 | Standard 32 KB 5200 cartridge                     |
 *   |  5 | 800/XL/XE |   32 | DB 32 KB cartridge                                |
 *   |  6 |   5200    |   16 | Two chip 16 KB 5200 cartridge                     |
 *   |  7 |   5200    |   40 | Bounty Bob Strikes Back 40 KB 5200 cartridge      |
 *   |  8 | 800/XL/XE |   64 | 64 KB Williams cartridge                          |
 *   |  9 | 800/XL/XE |   64 | Express 64 KB cartridge                           |
 *   | 10 | 800/XL/XE |   64 | Diamond 64 KB cartridge                           |
 *   | 11 | 800/XL/XE |   64 | SpartaDos X 64 KB cartridge                       |
 *   | 12 | 800/XL/XE |   32 | XEGS 32 KB cartridge                              |
 *   | 13 | 800/XL/XE |   64 | XEGS 64 KB cartridge                              |
 *   | 14 | 800/XL/XE |  128 | XEGS 128 KB cartridge                             |
 *   | 15 | 800/XL/XE |   16 | OSS 'M091' 16 KB cartridge                        |
 *   | 16 |   5200    |   16 | One chip 16 KB 5200 cartridge                     |
 *   | 17 | 800/XL/XE |  128 | Atrax 128 KB cartridge                            |
 *   | 18 | 800/XL/XE |   40 | Bounty Bob Strikes Back 40 KB cartridge           |
 *   | 19 |   5200    |    8 | Standard 8 KB 5200 cartridge                      |
 *   | 20 |   5200    |    4 | Standard 4 KB 5200 cartridge                      |
 *   | 21 |    800    |    8 | Right slot 8 KB cartridge                         |
 *   | 22 | 800/XL/XE |   32 | 32 KB Williams cartridge                          |
 *   | 23 | 800/XL/XE |  256 | XEGS 256 KB cartridge                             |
 *   | 24 | 800/XL/XE |  512 | XEGS 512 KB cartridge                             |
 *   | 25 | 800/XL/XE | 1024 | XEGS 1 MB cartridge                               |
 *   | 26 | 800/XL/XE |   16 | MegaCart 16 KB cartridge                          |
 *   | 27 | 800/XL/XE |   32 | MegaCart 32 KB cartridge                          |
 *   | 28 | 800/XL/XE |   64 | MegaCart 64 KB cartridge                          |
 *   | 29 | 800/XL/XE |  128 | MegaCart 128 KB cartridge                         |
 *   | 30 | 800/XL/XE |  256 | MegaCart 256 KB cartridge                         |
 *   | 31 | 800/XL/XE |  512 | MegaCart 512 KB cartridge                         |
 *   | 32 | 800/XL/XE | 1024 | MegaCart 1 MB cartridge                           |
 *   | 33 | 800/XL/XE |   32 | Switchable XEGS 32 KB cartridge                   |
 *   | 34 | 800/XL/XE |   64 | Switchable XEGS 64 KB cartridge                   |
 *   | 35 | 800/XL/XE |  128 | Switchable XEGS 128 KB cartridge                  |
 *   | 36 | 800/XL/XE |  256 | Switchable XEGS 256 KB cartridge                  |
 *   | 37 | 800/XL/XE |  512 | Switchable XEGS 512 KB cartridge                  |
 *   | 38 | 800/XL/XE | 1024 | Switchable XEGS 1 MB cartridge                    |
 *   | 39 | 800/XL/XE |    8 | Phoenix 8 KB cartridge                            |
 *   | 40 | 800/XL/XE |   16 | Blizzard 16 KB cartridge                          |
 *   | 41 | 800/XL/XE |  128 | Atarimax 128 KB Flash cartridge                   |
 *   | 42 | 800/XL/XE | 1024 | Atarimax 1 MB Flash cartridge                     |
 *   | 43 | 800/XL/XE |  128 | SpartaDos X 128 KB cartridge                      |
 *   +----+-----------+------+---------------------------------------------------+
 */

public class Cartridge {

    private static final int MACHINE_TYPE_5200 = 0;
    private static final int MACHINE_TYPE_XLXE = 1;

    private String _mapFile;

    private HashMap<String, byte[]> _cartMap = null;

    private static Cartridge _instance = new Cartridge();

    private Cartridge() {
        _mapFile =  android.os.Environment.getDataDirectory() + "/data/com.droid800/cartmap";
        _cartMap = null; // lazy load
    }

    public static Cartridge getInstance() {
        return _instance;
    }

    /**
     * This method
     * 1) inspects the file at cartridgePath to see if it is in CART format.
     *     If it is, then cartridgePath is returned.
     * 2) does an md5 sum of the file at cartridgePath to see if the
     *     cartridge path for this file is known. If the type is known,
     *     then the raw cartridge is wrapped in CART format and saved
     *     to disk. The path to the new cartridge is returned.
     * 3) prompts the user for the cartridge type. The raw cartridge is then
     *     wrapped in CART format and the path to the new cartridge is
     *     returned.
     */
    public String prepareCartridge(String path) {
        return prepareCartridge(path,MACHINE_TYPE_XLXE);
    }
    public String prepareCartridge(String path, int machineType) {
        // we do our best to find a cartridge here - otherwise we pass the
        // file along to the emulator untouched.
        try {
            if (isCartFormat(path)) {
                return path;
            }
            else {
                byte cartHeader[] = lookupCartHeader(path);
                if (null == cartHeader) {
                    // unknown rom. we will make an assumption about the cart
                    // type based on the size of the cart and the reported
                    // machinetype.
                    int checksum = atariChecksum(path, 0);
                    long fileSize = getCartSize(path);
                    if (machineType == MACHINE_TYPE_XLXE) {
                        if (fileSize == 8192) { // 8k cart
                            cartHeader = createCartHeader(1, checksum);
                        }
                        else if (fileSize == 16384) { // 16k cart
                            cartHeader = createCartHeader(2, checksum);
                        }
                        else if (fileSize == 32768) { // 32k cart
                            cartHeader = createCartHeader(5, checksum);
                        }
                    }
                    else {
                        if (fileSize == 4096) { // 5200 4k cart
                            cartHeader = createCartHeader(20, checksum);
                        }
                        else if (fileSize == 8192) { // 5200 8k cart
                            cartHeader = createCartHeader(19, checksum);
                        }
                        else if (fileSize == 16384) { // 5200 16k cart
                            cartHeader = createCartHeader(6, checksum);
                        }
                        else if (fileSize == 32768) { // 5200 32k cart
                            cartHeader = createCartHeader(4, checksum);
                        }
                    }
                    if (cartHeader == null) {
                        return path;
                    }
                    else {
                        return buildCartFile("/sdcard/temp.car", path, cartHeader);
                    }
                }
                else {
                    return buildCartFile("/sdcard/temp.car", path, cartHeader);
                }
            }
        }
        catch (Throwable e) {
            e.printStackTrace();
            return path;
        }
    }

    private long getCartSize(String path)
    throws Exception {
        try {
            File f = new File(path);
            return f.length();
        }
        catch (Throwable e) {
            throw new Exception("exception while getting file size for " + path, e);
        }
    }

    /**
     * Made an md5 of the raw rom file pointed to by path and see if we have
     * a cached CART entry for it.
     */
    private byte[] lookupCartHeader(String path)
    throws Exception {
        try {
            init();
            return _cartMap.get(getCartDigest(path));
        }
        catch (Throwable e) {
            throw new Exception("Exception while trying to deserialize HashMap", e);
        }
    }

    private String getCartDigest(String path)
    throws Exception {
        // calculate an md5 for the file.
        MessageDigest digest = MessageDigest.getInstance("MD5");
        FileInputStream f = null;
        try {
            f = new FileInputStream(path);
            byte [] buffer = new byte[8192];
            int bytesRead = 0;
            while ((bytesRead = f.read(buffer)) > 0) {
                digest.update(buffer, 0, bytesRead);
            }
            return getHexString(digest.digest());
        }
        catch (Throwable e) {
            throw new Exception("exception while calculating md5 on " + path, e);
        }
        finally {
            try {
                f.close();
            }
            catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Check if the file at 'path' starts with a cart header
     */
    private boolean isCartFormat(String path)
    throws Exception {
        FileInputStream fs = null;
        try {
            // check if the first 4 bytes spell CART
            fs = new FileInputStream(path);
            if (fs.read() != 67) return false; // C
            if (fs.read() != 65) return false; // A
            if (fs.read() != 82) return false; // R
            if (fs.read() != 84) return false; // T

            // extract the ehcksum from the header
            byte [] checksumBytes = new byte[4];
            fs.read(checksumBytes);
            fs.read(checksumBytes);
            fs.close();
            fs = null;
            // if the checksums match we have a CART
            if (decodeInt(checksumBytes, 0) == atariChecksum(path, 16)) {
                return true;
            }

            return false;
        }
        catch (Throwable e) {
            throw new Exception("Exception in isCartFormat", e);
        }
        finally {
            if (fs != null) {
                try {
                    fs.close();
                }
                catch (Throwable e) {
                    throw new Exception("Exception closing stream", e);
                }
            }
        }
    }

    public int decodeInt(byte[] buf, int offset)
    throws Exception {
        return (int)
            ((buf[offset+0] &   0x000000FF)<<24) |
            ((buf[offset+1] & 0x000000FF)<<16) |
            ((buf[offset+2] & 0x000000FF)<<8) |
            ((buf[offset+3] & 0x000000FF));
    }

    public int encodeInt(byte[] buf, int offset, int value)
    throws Exception {
        buf[offset+0] = (byte) (value >> 24);
        buf[offset+1] = (byte) (value >> 16);
        buf[offset+2] = (byte) (value >> 8);
        buf[offset+3] = (byte) (value);
        return 4;
    }

    private byte[] createCartHeader(int cartType, int checksum)
    throws Exception {
        byte cartHeader[] = new byte[16];
        cartHeader[0] =  (byte)67;
        cartHeader[1] =  (byte)65;
        cartHeader[2] =  (byte)82;
        cartHeader[3] =  (byte)84;

        encodeInt(cartHeader, 4, cartType);
        encodeInt(cartHeader, 8, checksum);

        return cartHeader;
    }

    private String buildCartFile(String newFileName, String path, byte cartHeader[]) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(path);
            File f = new File(newFileName);
            f.createNewFile();
            fos = new FileOutputStream(f);
            byte buffer[] = new byte[1024];
            fos.write(cartHeader);

            int bytesRead = 0;
            while ((bytesRead = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, bytesRead);
            }
            fis.close();
            fos.close();
            return newFileName;
        }
        catch (Throwable e) {
            e.printStackTrace();
            try {
                if (null != fis) fis.close();
                if (null != fos) fos.close();
            }
            catch (Throwable ee) {
                ee.printStackTrace();
            }
            return null;
        }
    }

    private void init() {
        try {
            if (_cartMap != null) return;
            // check to see if the cartmap file exists, if not then we
            // create one with known values.
            File f = new File(_mapFile);
            if (f.exists()) {
                loadCartmap();
            }
            else {
                _cartMap = new HashMap<String,byte[]>();
                _cartMap.put("d7b386090a0568b4f3bd62662f71b330", createCartHeader(4, 3612958)); // ./Choplifter.bin
                _cartMap.put("8f4c07a9e0ef2ded720b403810220aaf", createCartHeader(4, 3159490)); // ./CastleCrisis_5200.bin
                _cartMap.put("5048a304302deea79257a03f409bb8d5", createCartHeader(4, 4771658)); // ./Roadrunner.bin
                _cartMap.put("d00dff571bfa57c7ff7880c3ce03b178", createCartHeader(4, 4419952)); // ./MarioBros.bin
                _cartMap.put("9002ffcfcbcc25e4d6e07925707427a6", createCartHeader(4, 7104642)); // ./SuperBreakout.bin
                _cartMap.put("53d0fa90d913084677212a240c5affbd", createCartHeader(4, 3921153)); // ./xevious.bin
                _cartMap.put("303442c54d579185b1c82e80e8ce8362", createCartHeader(4, 3412796)); // ./RealsportsBaseball.bin
                _cartMap.put("f5cd178cbea0ae7d8cf65b30cfd04225", createCartHeader(4, 3740998)); // ./Ballblazer.bin
                _cartMap.put("030d43703157fab023eadef6faba484c", createCartHeader(4, 2789760)); // ./Countermeasure.bin
                _cartMap.put("36bfd7c52ffc566b5da8bd3a8bd99d5d", createCartHeader(4, 3276348)); // ./Robotron.bin
                _cartMap.put("89b498b040c5760f554dca6302581685", createCartHeader(4, 3194994)); // ./Looney Tunes Hotel.bin
                _cartMap.put("6b76413ec7ed290ce05afef90d84f2ab", createCartHeader(4, 3750714)); // ./Stargate.bin
                _cartMap.put("cafda13dd60048b5f4f8e3b78eb9b6fc", createCartHeader(4, 1562536)); // ./SportGoofy.bin
                _cartMap.put("8688d478799d51eeaec118a6666bb33d", createCartHeader(4, 4050119)); // ./Gremlins.bin
                _cartMap.put("b306e288bdb60ebe0e2e80ccfe2c54d7", createCartHeader(4, 3145384)); // ./MountainKing.bin
                _cartMap.put("6d4131b9f097812417bb0889c6ce8470", createCartHeader(4, 2838138)); // ./StarTrek.bin
                _cartMap.put("4360242a72b708031bd85b4fbc362132", createCartHeader(4, 3210624)); // ./Popeye.bin
                _cartMap.put("fc71d2850be34f6a0404667e6af375af", createCartHeader(4, 6045701)); // ./WizardOfWor.bin
                _cartMap.put("1442319e10c920631d916c4e881b3d2a", createCartHeader(4, 3374090)); // ./Joust.bin
                _cartMap.put("6af0a3c0eeda144a6b3d11dbde1bcacb", createCartHeader(4, 3515010)); // ./DigDug.bin
                _cartMap.put("54d34a7926cffcd7778f38fc3fceb960", createCartHeader(4, 4527860)); // ./Qix.bin
                _cartMap.put("0058be79382ee15d216905366067f18f", createCartHeader(4, 6034144)); // ./SuperCobra.bin
                _cartMap.put("c98ed8fe7c49744f0a139f5dc58bfcda", createCartHeader(4, 4443278)); // ./PacMan.bin
                _cartMap.put("221d943b1043f5bdf2b0f25282183404", createCartHeader(4, 2122055)); // ./spitfire.bin
                _cartMap.put("2c7aa7edfbc5c21997991616f7f321a1", createCartHeader(4, 3479192)); // ./Miner2049er.bin
                _cartMap.put("3a570ffd0e1e0701a47b5d93d715e399", createCartHeader(4, 3485950)); // ./MontezumasRevenge.bin
                _cartMap.put("070a3827cb29379475b956111b1244f9", createCartHeader(4, 5727160)); // ./SpaceInvaders.bin
                _cartMap.put("bc33c07415b42646cc813845b979d85a", createCartHeader(4, 4157964)); // ./Meebzork.bin
                _cartMap.put("c510002dccb798df1fa5523bb648b32f", createCartHeader(4, 2856338)); // ./MsPacMan.bin
                _cartMap.put("6781b3d836693436e6fcd4ebc54085cd", createCartHeader(4, 3357328)); // ./Meteorites.bin
                _cartMap.put("1dee7cb5ae881520c6663c9fc8fce986", createCartHeader(4, 3675640)); // ./ReturnOfTheJedi.bin
                _cartMap.put("e3fbad9d967cec0d8756d9c07346a8fb", createCartHeader(4, 4565157)); // ./BarroomBaseball.bin
                _cartMap.put("a301a449fc20ad345b04932d3ca3ef54", createCartHeader(4, 4071283)); // ./Pengo.bin
                _cartMap.put("affaaa081cc47aee3caca2f9b3f6787d", createCartHeader(4, 2845270)); // ./Kangaroo.bin
                _cartMap.put("3b03e3cda8e8aa3beed4c9617010b010", createCartHeader(4, 3302369)); // ./koffipc.bin
                _cartMap.put("5f187c5c79e93866c92289c2e595ad74", createCartHeader(4, 3366300)); // ./Millipede.bin
                _cartMap.put("556a66d6737f0f793821e702547bc051", createCartHeader(4, 4414400)); // ./Vanguard.bin
                _cartMap.put("497aa2c9a31ddaad70aa4cd7ca22b6bf", createCartHeader(4, 3558182)); // ./JungleHunt.bin
                _cartMap.put("12618566bf6a7e88ac9c147524b183a5", createCartHeader(4, 3190945)); // ./Zaxxon.bin
                _cartMap.put("1d75deb695cc73350f402c0092f70e87", createCartHeader(4, 3282948)); // ./QuestForQuintanaRoo.bin
                _cartMap.put("908898bbf914ef55905490b103a9a18d", createCartHeader(4, 5659490)); // ./MissileCommand.bin
                _cartMap.put("e6cb66bd9add2427441433a0dbd60664", createCartHeader(4, 5754224)); // ./Berzerk.bin
                _cartMap.put("59ccd82910e6d4316d737a9b282ed851", createCartHeader(4, 4181300)); // ./RealsportsSoccer.bin
                _cartMap.put("0dc44c5bf0829649b7fec37cb0a8186b", createCartHeader(4, 3550609)); // ./RescueOnFractulus.bin
                _cartMap.put("6d2296cada88c2f286bebe909559adcb", createCartHeader(4, 3482478)); // ./laststrf.bin
                _cartMap.put("a61d916f5e510b516f04aa1259d36ae4", createCartHeader(4, 5885550)); // ./Galaxian.bin
                _cartMap.put("c38adbb9efdd80d5a9c44ad9274a7e79", createCartHeader(4, 3672394)); // ./Defender.bin
                _cartMap.put("5b3a198a6ad4e90500861dac5bb35914", createCartHeader(4, 3962130)); // ./StarRaiders.bin
                _cartMap.put("428f76e23cb0a499b396824ce0ff70e7", createCartHeader(4, 3063670)); // ./BlackBelt.bin
                _cartMap.put("b983d987ffd2df2a01bf0a811c1c9c9a", createCartHeader(4, 5925755)); // ./BluePrint.bin
                _cartMap.put("81790daff7f7646a6c371c056622be9c", createCartHeader(18, 4232690)); // ./bigFive/BountyBobStrikesBack.bin
                _cartMap.put("2bb63d65efc8682bc4dfac0fd0a823be", createCartHeader(16, 1657005)); // ./oneChip16/FinalLegacy.bin
                _cartMap.put("453015c519ae345a4ef8768f3d119056", createCartHeader(16, 1739886)); // ./oneChip16/KrazyShootOut.bin
                _cartMap.put("496b6a002bc7d749c02014f7ec6c303c", createCartHeader(16, 1059759)); // ./oneChip16/Tempst52.bin
                _cartMap.put("e0b47a17fa6cd9d6addc1961fca43414", createCartHeader(16, 1648780)); // ./oneChip16/blaster_5200.bin
                _cartMap.put("08355ef11e90509762a7a25a4b62ddca", createCartHeader(16, 435932)); // ./oneChip16/yellwsub.bin
                _cartMap.put("f817453412f39d7f8f4b2fd2848124c5", createCartHeader(16, 1792032)); // ./oneChip16/Mr. Do's Castle.bin
                _cartMap.put("cf45e6e5d9b00ef3270488185bc1e4b5", createCartHeader(16, 1358273)); // oneChip16/asteroid.bin
                _cartMap.put("6e24e3519458c5cb95a7fd7711131f8d", createCartHeader(6, 1722560)); // ./twoChip16/SpaceDungeon.bin
                _cartMap.put("022c47b525b058796841134bb5c75a18", createCartHeader(6, 1750520)); // ./twoChip16/RealsportsFootball.bin
                _cartMap.put("7e683e571cbe7c77f76a1648f906b932", createCartHeader(6, 2064850)); // ./twoChip16/RealsportsTennis.bin
                _cartMap.put("dacc0a82e8ee0c086971f9d9bac14127", createCartHeader(6, 1536571)); // ./twoChip16/Gyruss.bin
                _cartMap.put("592cf6e409a90d6fda0276e85c254d55", createCartHeader(6, 1807732)); // ./twoChip16/Qbert.bin
                _cartMap.put("32a6d0de4f1728dee163eb2d4b3f49f1", createCartHeader(6, 2510822)); // ./twoChip16/Diagnostic Cart.bin
                _cartMap.put("4f6c58c28c41f31e3a1515fe1e5d15af", createCartHeader(6, 1452309)); // ./twoChip16/xari52.bin
                _cartMap.put("8576867c2cfc965cf152be0468f684a7", createCartHeader(6, 1430301)); // ./twoChip16/Battlezone.bin
                _cartMap.put("a074a1ff0a16d1e034ee314b85fa41e9", createCartHeader(6, 1565177)); // ./twoChip16/BuckRogers.bin
                _cartMap.put("a71bfb11676a4e4694af66e311721a1b", createCartHeader(6, 1732173)); // ./twoChip16/RealsportsBasketballRev1.bin
                _cartMap.put("88ea120ef17747d7b567ffa08b9fb578", createCartHeader(6, 1759965)); // ./twoChip16/CongoBongo.bin
                _cartMap.put("65a2c585d80ba363f22b5ab7309e71bc", createCartHeader(6, 1889254)); // ./twoChip16/Gorf.bin
                _cartMap.put("14bd9a0423eafc3090333af916cfbce6", createCartHeader(6, 1312312)); // ./twoChip16/frskytom.bin
                _cartMap.put("d89669f026c34de7f0da2bcb75356e27", createCartHeader(6, 1538121)); // ./twoChip16/SuperPacManFinal.bin
                _cartMap.put("936db7c08e6b4b902c585a529cb15fc5", createCartHeader(6, 1717758)); // ./twoChip16/JamesBond007.bin
                _cartMap.put("fd0cbea6ad18194be0538844e3d7fdc9", createCartHeader(6, 1671269)); // ./twoChip16/PolePosition.bin
                _cartMap.put("d1a3b6613b03716af6aefe21d1bfdf07", createCartHeader(6, 1746374)); // ./twoChip16/frogger2.bin
                _cartMap.put("69ffa4707c786a67bcef8364da4777d6", createCartHeader(6, 316276)); // ./twoChip16/boogie.bin
                _cartMap.put("00beaa8405c7fb90d86be5bb1b01ea66", createCartHeader(6, 1517184)); // ./twoChip16/StarWarsArcade.bin
                _cartMap.put("261702e8d9acbf45d44bb61fd8fa3e17", createCartHeader(6, 1596285)); // ./twoChip16/Centipede.bin
                _cartMap.put("bd7e6aa528e2eb37d8af28e174220c2c", createCartHeader(6, 1676538)); // ./twoChip16/Frogger.bin
                _cartMap.put("e056001d304db597bdd21b2968fcc3e6", createCartHeader(6, 2269492)); // ./twoChip16/RealsportsBasketballFinal.bin
                saveCartmap();
                // create a new cartmap and save it
            }
        }
        catch (Throwable  e) {
            e.printStackTrace();
        }
    }

    private void loadCartmap()
    throws Exception {
        ObjectInput oi = null;
        try {
            // look for a frozen map - otherwise create an empty one
            oi = new ObjectInputStream(new FileInputStream(_mapFile));
            _cartMap = (HashMap<String,byte[]>) oi.readObject();
            oi.close();
            oi = null;
        }
        catch (Throwable e) {
            throw new Exception("Exception loading cartmap", e);
        }
        finally {
            try {
                if (null != oi) oi.close();
            }
            catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    private void saveCartmap()
    throws Exception {
        ObjectOutput oo = null;
        try {
            // look for a frozen map - otherwise create an empty one
            File f = new File(_mapFile);
            f.createNewFile();
            oo = new ObjectOutputStream(new FileOutputStream(f));
            oo.writeObject(_cartMap);
            oo.close();
            oo = null;
        }
        catch (Throwable e) {
            throw new Exception("Exception saving cartmap", e);
        }
        finally {
            try {
                if (null != oo) oo.close();
            }
            catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    static final byte[] HEX_CHAR_TABLE = {
        (byte)'0', (byte)'1', (byte)'2', (byte)'3',
        (byte)'4', (byte)'5', (byte)'6', (byte)'7',
        (byte)'8', (byte)'9', (byte)'a', (byte)'b',
        (byte)'c', (byte)'d', (byte)'e', (byte)'f'
    };

    public static String getHexString(byte[] raw)
    throws Exception
    {
        byte[] hex = new byte[2 * raw.length];
        int index = 0;

        for (byte b : raw) {
            int v = b & 0xFF;
            hex[index++] = HEX_CHAR_TABLE[v >>> 4];
            hex[index++] = HEX_CHAR_TABLE[v & 0xF];
        }
        return new String(hex, "ASCII");
    }


    /**
     * given a rom file, dump the md5 and the checksum.
     *
     *
     */
    public static void main(String argv[]) {
        try {
            Cartridge cartridge = Cartridge.getInstance();
            System.out.println("_cartMap.put(\""
                + cartridge.getCartDigest(argv[0]) + "\", createCartHeader("
                + argv[1] + ", " + cartridge.atariChecksum(argv[0], 0)
                + ")); // " + argv[0]);
            // and now write out the cart.
            byte cartHeader[] = cartridge.createCartHeader(4, cartridge.atariChecksum(argv[0], 0));
            cartridge.buildCartFile(argv[0] + ".car", argv[0], cartHeader);
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private int atariChecksum(String filePath, int offset) {
        FileInputStream f = null;
        try {
            int cksum = 0;
            f = new FileInputStream(filePath);
            int b = -1;
            // we only calculate the checksum for the rom data, starting
            // at offset 16
            f.skip(offset);
            while ((b = f.read()) > -1) {
                cksum += b;
            }
            return cksum;
        }
        catch (Throwable e) {
            e.printStackTrace();
            return -1;
        }
        finally {
            if (null != f) {
                try {
                    f.close();
                }
                catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }


}

