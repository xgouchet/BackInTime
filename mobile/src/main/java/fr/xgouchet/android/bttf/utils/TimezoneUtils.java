package fr.xgouchet.android.bttf.utils;

import android.content.Context;
import android.preference.ListPreference;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 *
 */
public class TimezoneUtils {

    private static final List<String> TIMEZONE_CODES = new ArrayList<String>() {{
        add("ACDT");
        add("ACST");
        add("ACT");
        add("ADT");
        add("AEDT");
        add("AEST");
        add("AFT");
        add("AKDT");
        add("AKST");
        add("AMST");
        add("AMT");
        add("ART");
        add("AST");
        add("AWDT");
        add("AWST");
        add("AZOST");
        add("AZT");
        add("BDT");
        add("BIOT");
        add("BIT");
        add("BOT");
        add("BRT");
        add("BST");
        add("BTT");
        add("CAT");
        add("CCT");
        add("CDT");
        add("CEDT");
        add("CEST");
        add("CET");
        add("CHADT");
        add("CHAST");
        add("CHOT");
        add("CHST");
        add("CHUT");
        add("CIST");
        add("CIT");
        add("CKT");
        add("CLST");
        add("CLT");
        add("COST");
        add("COT");
        add("CST");
        add("CST6CDT");
        add("CT");
        add("CVT");
        add("CWST");
        add("CXT");
        add("DAVT");
        add("DDUT");
        add("DFT");
        add("EASST");
        add("EAST");
        add("EAT");
        add("ECT");
        add("EDT");
        add("EEDT");
        add("EEST");
        add("EET");
        add("EGS");
        add("EGT");
        add("EIT");
        add("EST");
        add("EST5EDT");
        add("FET");
        add("FJT");
        add("FKST");
        add("FKT");
        add("FNT");
        add("GALT");
        add("GAMT");
        add("GET");
        add("GFT");
        add("GILT");
        add("GIT");
        add("GMT");
        add("GMT0");
        add("GMT+0");
        add("GMT-0");
        add("GST");
        add("GYT");
        add("HADT");
        add("HAEC");
        add("HAST");
        add("HKT");
        add("HMT");
        add("HOVT");
        add("HST");
        add("ICT");
        add("IDT");
        add("IOT");
        add("IRDT");
        add("IRKT");
        add("IRST");
        add("IST");
        add("JST");
        add("KGT");
        add("KOST");
        add("KRAT");
        add("KST");
        add("LHST");
        add("LINT");
        add("MAGT");
        add("MART");
        add("MAWT");
        add("MDT");
        add("MET");
        add("MEST");
        add("MHT");
        add("MIST");
        add("MIT");
        add("MMT");
        add("MSK");
        add("MST");
        add("MST7MDT");
        add("MUT");
        add("MVT");
        add("MYT");
        add("NCT");
        add("NDT");
        add("NFT");
        add("NPT");
        add("NST");
        add("NT");
        add("NUT");
        add("NZDT");
        add("NZST");
        add("OMST");
        add("ORAT");
        add("PDT");
        add("PET");
        add("PETT");
        add("PGT");
        add("PHOT");
        add("PKT");
        add("PMDT");
        add("PMST");
        add("PONT");
        add("PST");
        add("PST");
        add("PYST");
        add("PYT");
        add("RET");
        add("ROTT");
        add("SAKT");
        add("SAMT");
        add("SAST");
        add("SBT");
        add("SCT");
        add("SGT");
        add("SLST");
        add("SRT");
        add("SST");
        add("SYOT");
        add("TAHT");
        add("THA");
        add("TFT");
        add("TJT");
        add("TKT");
        add("TLT");
        add("TMT");
        add("TOT");
        add("TVT");
        add("UCT");
        add("ULAT");
        add("UTC");
        add("UYST");
        add("UYT");
        add("UZT");
        add("VET");
        add("VLAT");
        add("VOLT");
        add("VOST");
        add("VUT");
        add("WAKT");
        add("WAST");
        add("WAT");
        add("WEDT");
        add("WEST");
        add("WET");
        add("WIT");
        add("WST");
        add("W-SU");
        add("YAKT");
        add("YEKT");
        add("Z");
    }};

    private static final String KEY_ID = "id";
    private static final String KEY_GMT = "gmt";
    private static final String KEY_OFFSET = "offset";


    public static void setupTimezonPreference(ListPreference preference) {

        // create a list of maps containing everything about the available time zoines
        List<String> values = new ArrayList<String>();

        String[] ids = TimeZone.getAvailableIDs();
        for (String id : ids) {

            // ignore all "Etc/GMT+2" ids
            if (id.startsWith("Etc/")) {
                continue;
            }

            // ignore coded timezones
            if ((!id.contains("/")) && (TIMEZONE_CODES.contains(id))) {
                continue;
            }

            values.add(id);
        }

        ids = values.toArray(new String[values.size()]);

        preference.setEntries(ids);
        preference.setEntryValues(ids);
    }
}
