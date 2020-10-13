package com.mosaicatm.fuser.updaters.pre;

import java.util.Objects;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.updaters.AbstractUpdater;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class GateAssignmentsUpdater
extends AbstractUpdater<MatmFlight, MatmFlight>
{
    private final Pattern LEADING_ZERO_PATTERN;
    private final Pattern INVALID_CHAR_PATTERN;
    private final Pattern PREFIX_CHECK_PATTERN;
    private final Pattern POSTFIX_CHECK_PATTERN;
    private final Pattern MIDDLE_CHECK_PATTERN;

    private final Pattern PREFIX_INTL_PATTERN;
    private final Pattern PREFIX_DIGIT_PATTERN;

    private final Pattern INTL;
    private final Pattern DIGIT;
    
    private final Log log = LogFactory.getLog(getClass());

    public GateAssignmentsUpdater()
    {
        // string starts with 0 or 0 is after a letter
        LEADING_ZERO_PATTERN = Pattern.compile("(^|[^0-9])0+");

        // character is not letter, digit, -, and _
        INVALID_CHAR_PATTERN = Pattern.compile("[^a-zA-Z0-9_-]");
        
        // String starts with character - or _
        PREFIX_CHECK_PATTERN = Pattern.compile("^[-_]+");
        
        // String ends with character - or _
        POSTFIX_CHECK_PATTERN = Pattern.compile("[-_]+$");
        
        // In the middle of the string, there is more than one - or _
        MIDDLE_CHECK_PATTERN = Pattern.compile("([-_]){2,}");

        // String starts with 'INTL', case insensitive
        PREFIX_INTL_PATTERN = Pattern.compile("(?i)^INTL\\w*");

        // String starts with 'a digit'
        PREFIX_DIGIT_PATTERN = Pattern.compile("^\\d[A-Z]\\w+");
        
        // Pattern used to replace the string with PREFIX_INTL_PATTERN pattern
        // case insensitive
        INTL = Pattern.compile("(?i)^INTL");
        
        // Pattern used to replace the string with PREFIX_DIGIT_PATTERN pattern
        DIGIT = Pattern.compile("^\\d");
    }
    
    @Override
    public void update(MatmFlight update, MatmFlight target)
    {
        if (!isActive())
        {
            return;
        }
        
        if(update == null)
        {
            log.error("Cannot update gate. Update is NULL!");
            return;
        }
        
        update.setArrivalStandActual(processStand(update.getArrivalStandActual(), target));
        update.setArrivalStandUser(processStand(update.getArrivalStandUser(), target));
        update.setArrivalStandAirline(processStand(update.getArrivalStandAirline(), target));
        update.setArrivalStandPositionDerived(processStand(update.getArrivalStandPositionDerived(), target));
        update.setArrivalStandModel(processStand(update.getArrivalStandModel(), target));
        update.setArrivalStandDecisionTree(processStand(update.getArrivalStandDecisionTree(), target));
        
        update.setDepartureStandActual(processStand(update.getDepartureStandActual(), target));
        update.setDepartureStandUser(processStand(update.getDepartureStandUser(), target));
        update.setDepartureStandAirline(processStand(update.getDepartureStandAirline(), target));
        update.setDepartureStandPositionDerived(processStand(update.getDepartureStandPositionDerived(), target));
        update.setDepartureStandModel(processStand(update.getDepartureStandModel(), target));
        update.setDepartureStandDecisionTree(processStand(update.getDepartureStandDecisionTree(), target));
    }
    
    private String processStand(String oldGate, MatmFlight flight)
    {
        String rtnGate = stripLeadingZeroes(oldGate);
        rtnGate = removeInvalidCharacter(rtnGate);
        rtnGate = validateGate(rtnGate);
        
        if(log.isDebugEnabled() && !Objects.equals(oldGate, rtnGate))
        {
            String gufi = null;
            if(flight != null)
            {
                gufi = flight.getGufi();
            }
            log.debug(gufi + ", stand " + oldGate + " contained illegal characters, converted to " + rtnGate);
        }
        return rtnGate;
    }

    /**
     * Remove characters in the string matching the pattern
     * @param pattern pattern to find in the string
     * @param s String to remove patterns from
     * @param replacement String to replace the pattern with
     * @return String with the patterns replaced with replacement string
     */
    private String remove(Pattern pattern, String s, String replacement) 
    {
        String res = null;
        if(validString(s)) {
            res = pattern.matcher(s).replaceAll(replacement);
        }
        return res;
    }
    
    /**
     * Remove leading zeros in the string
     * It's a leading zero if 0 comes after a letter or digit starts after 0
     * @param s String to strip leading zeros from
     * @return String without leading zeros
     */
    private String stripLeadingZeroes(String s) {
        return remove(LEADING_ZERO_PATTERN, s, "$1");
    }
    
    /**
     * Remove invalid characters in the string 
     * Invalid characters are any character which is not letter, digit, -, and _ 
     * @param s String to remove invalid characters from
     * @return String without invalid characters
     */
    public String removeInvalidCharacter(String s)
    {
        return remove(INVALID_CHAR_PATTERN, s, "");
    }
  
    /**
     * Checks if s has the matching patterns in it
     * If it does, replace the patterns accordingly
     * @param s gate name to be checked
     * @return New gate name
     */
    public String validateGate(String s) 
    {
        // String starts with character - or 
        String rtn = remove(PREFIX_CHECK_PATTERN, s, "");
        
        // String ends with character - or _
        rtn = remove(POSTFIX_CHECK_PATTERN, rtn, "");
        
        // In the middle of the string, there is more than one - or _
        rtn = remove(MIDDLE_CHECK_PATTERN, rtn, "$1");

        // if leading chars are "INTL"
        if(isMatchingPattern(PREFIX_INTL_PATTERN, rtn))
        {
            rtn = remove(INTL, rtn, "");
        }
        
        // if leading char is a digit followed by a any char
        if(isMatchingPattern(PREFIX_DIGIT_PATTERN, rtn))
        {
            rtn = remove(DIGIT, rtn, "");
        }
        
        return rtn;
    }

    private boolean isMatchingPattern(Pattern p, String s) {
        return validString(s) && p.matcher(s).matches();
    }
    
    private boolean validString(String s)
    {
        return s != null && !s.trim().isEmpty();
    }
}
