package message;

import static org.junit.Assert.assertTrue;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

import static message.Messages.*;
import physics.Vect;
import physics.Geometry.VectPair;

/**
 * Testing strategy
 * 
 * Test each public method with a few positive and negative messages
 * Checks corner cases and invalid characters
 * Makes sure all parser methods can correctly parse messages
 * Makes sure all parser methods reject invalid messages
 * 
 */
public class MessageTests {
    
    //Tests to make sure system can accept valid names
    @Test public void testIsValidNameGood(){
        String name1 = "A_8bqx213_DEFAULT";
        String name2 = "fran";
        assertTrue(isValidName(name1));
        assertTrue(isValidName(name2));
    }
    
    //Tests to make sure system can reject invalid names
    @Test public void testIsValidNameBad(){
        String name1 = "A_8bqx213_DEFAULT ";
        String name2 = "fran-k&&^";
        String name3 = "`~!@#$%^&*()-=+[]{}><.,/? \\\'\";:";
        String name4 = "4dam";
        assertFalse(isValidName(name1));
        assertFalse(isValidName(name2));
        assertFalse(isValidName(name4));
        for(int ii = 0; ii < name3.length()-1; ii ++){
            assertFalse(isValidName(name3.substring(ii, ii+1)));
        }
    }
    
    //Tests to make sure system can recognize valid messages
    @Test public void testIsValidMessageGood(){
        String message1 = "fran--->bob:HELLO:";
        String message2 = "P_as0945--->c99999999:WALL_TELEPORT:b.lah -blah blah";
        String message3 = "aslkdf--->fulk:DISCONNECT:fulk ";
        String message4 = "roger--->corn:DELINK:bob TOP";
        assertTrue(isValidMessage(message1));
        assertTrue(isValidMessage(message2));
        assertTrue(isValidMessage(message3));
        assertTrue(isValidMessage(message4));
    }
    
    //Tests to make sure system can reject invalid messages
    @Test public void testIsValidMessageBad(){
        String message1 = "fran---->bob:HELLO:";
        String message2 = "P_as0945--->c99999999:HELLO";
        String message3 = "9--->bob:DISCONNECT:";
        String message4 = "farm--->cow:BOB";
        String message5 = "vladimir--->dracula:LINK:&%";
        assertFalse(isValidMessage(message1));
        assertFalse(isValidMessage(message2));
        assertFalse(isValidMessage(message3));
        assertFalse(isValidMessage(message4));
        assertFalse(isValidMessage(message5));
    }
    
    //Tests to make sure parseType can correctly parse messages into the correct 
    //type of message
    @Test public void testParseType(){
        String message1 = "fran--->bob:HELLO:";
        String message2 = "P_as0945--->c99999999:WALL_TELEPORT:b.lah blah blah";
        String message3 = "aslkdf--->fulk:DISCONNECT:fulk ";
        String message4 = "farm--->cow:BOB:";
        assertTrue(parseType(message1) == MessageType.HELLO);
        assertTrue(parseType(message2) == MessageType.WALL_TELEPORT);
        assertTrue(parseType(message3) == MessageType.DISCONNECT);
        assertTrue(parseType(message4) == MessageType.INVALID);
    }
    
    //Tests to make sure parseSender can correctly parse messages
    @Test public void testParseSender(){
        String message1 = "fran--->bob:HELLO:";
        String message2 = "P_as0945--->c99999999:WALL_TELEPORT:b.lah blah blah";
        String message3 = "aslkdf--->fulk:DISCONNECT:fulk ";
        assertTrue(parseSender(message1).equals("fran"));
        assertTrue(parseSender(message2).equals("P_as0945"));
        assertTrue(parseSender(message3).equals("aslkdf"));
    }
    
    //checks to make sure parseReceiver can correctly parse messages
    @Test public void testParseReceiver(){
        String message1 = "fran--->bob:HELLO:";
        String message2 = "P_as0945--->c99999999:WALL_TELEPORT:b.lah blah blah";
        String message3 = "aslkdf--->fUlk:DISCONNECT:fulk ";
        assertTrue(parseReceiver(message1).equals("bob"));
        assertTrue(parseReceiver(message2).equals("c99999999"));
        assertTrue(parseReceiver(message3).equals("fUlk"));
    }
    
    //Tests to make sure parseArguments can correctly parse messages
    @Test public void testParseArgs(){
        String message1 = "fran--->bob:HELLO:";
        String message2 = "P_as0945--->c99999999:WALL_TELEPORT:b.lah blah blah";
        String message3 = "aslkdf--->fUlk:DISCONNECT:fulk ";
        assertTrue(parseArguments(message1).length == 0);
        assertTrue(parseArguments(message2)[0].equals("b.lah"));
        assertTrue(parseArguments(message2)[1].equals("blah"));
        assertTrue(parseArguments(message3)[0].equals("fulk"));
    }
    
    //Tests to make sure parseWallTeleportArguments can correctly parse good messages
    //and reject bad messages
    @Test public void testParseWallTeleportMsgArgs(){
        String messageGood1 = "P_as0945--->c99999999:WALL_TELEPORT:1.3 5 27 -3";
        String messageGood2 = "bob--->frank:WALL_TELEPORT:2.2345 -4    2  1 ";
        String messageBad1 = "bill--->ted:WALL_TELEPORT:widdershins 1 1 1 1";
        String messageBad2 = "keanu--->morpheus:WALL_TELEPORT:1... 3 4 5";
        String messageGood3 = "something--->someone:WALL_TELEPORT:0.2E6 5 2 3";
        VectPair vects1 = Messages.parseWallTeleportArguments(messageGood1);
        VectPair vects2 = Messages.parseWallTeleportArguments(messageGood2);
        assertTrue(vects1.v1.equals(new Vect(1.3,5)) && vects1.v2.equals(new Vect(27, -3)));
        assertTrue(vects2.v1.equals(new Vect(2.2345,-4)) && vects2.v2.equals(new Vect(2,1)));
        try {
            @SuppressWarnings("unused")
            VectPair vects = Messages.parseWallTeleportArguments(messageGood3);
            assertTrue(true);
        }catch(Exception e){
            assertTrue(false);
        } try {
            @SuppressWarnings("unused")
            VectPair vects = Messages.parseWallTeleportArguments(messageBad1);
            assertTrue(false);
        }catch(Exception e){
            assertTrue(true);
        } try {
            @SuppressWarnings("unused")
            VectPair vects = Messages.parseWallTeleportArguments(messageBad2);
            assertTrue(false);
        }catch(Exception e){
            assertTrue(true);
        }
    }
    
}
