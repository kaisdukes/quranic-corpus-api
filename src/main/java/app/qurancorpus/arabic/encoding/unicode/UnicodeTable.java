package app.qurancorpus.arabic.encoding.unicode;

import app.qurancorpus.arabic.CharacterType;
import app.qurancorpus.arabic.DiacriticType;
import app.qurancorpus.arabic.encoding.EncodingTableBase;

public class UnicodeTable extends EncodingTableBase {
    public static final UnicodeTable UNICODE_TABLE = new UnicodeTable();

    private UnicodeTable() {
        addItem(UnicodeType.Hamza, (char) 1569, CharacterType.Hamza);
        addItem(UnicodeType.AlifWithMaddah, (char) 1570, CharacterType.Alif, DiacriticType.Maddah);
        addItem(UnicodeType.AlifWithHamzaAbove, (char) 1571, CharacterType.Alif, DiacriticType.HamzaAbove);
        addItem(UnicodeType.WawWithHamzaAbove, (char) 1572, CharacterType.Waw, DiacriticType.HamzaAbove);
        addItem(UnicodeType.AlifWithHamzaBelow, (char) 1573, CharacterType.Alif, DiacriticType.HamzaBelow);
        addItem(UnicodeType.YaWithHamzaAbove, (char) 1574, CharacterType.Ya, DiacriticType.HamzaAbove);
        addItem(UnicodeType.Alif, (char) 1575, CharacterType.Alif);
        addItem(UnicodeType.Ba, (char) 1576, CharacterType.Ba);
        addItem(UnicodeType.TaMarbuta, (char) 1577, CharacterType.TaMarbuta);
        addItem(UnicodeType.Ta, (char) 1578, CharacterType.Ta);
        addItem(UnicodeType.Tha, (char) 1579, CharacterType.Tha);
        addItem(UnicodeType.Jeem, (char) 1580, CharacterType.Jeem);
        addItem(UnicodeType.HHa, (char) 1581, CharacterType.HHa);
        addItem(UnicodeType.Kha, (char) 1582, CharacterType.Kha);
        addItem(UnicodeType.Dal, (char) 1583, CharacterType.Dal);
        addItem(UnicodeType.Thal, (char) 1584, CharacterType.Thal);
        addItem(UnicodeType.Ra, (char) 1585, CharacterType.Ra);
        addItem(UnicodeType.Zain, (char) 1586, CharacterType.Zain);
        addItem(UnicodeType.Seen, (char) 1587, CharacterType.Seen);
        addItem(UnicodeType.Sheen, (char) 1588, CharacterType.Sheen);
        addItem(UnicodeType.Sad, (char) 1589, CharacterType.Sad);
        addItem(UnicodeType.DDad, (char) 1590, CharacterType.DDad);
        addItem(UnicodeType.TTa, (char) 1591, CharacterType.TTa);
        addItem(UnicodeType.DTha, (char) 1592, CharacterType.DTha);
        addItem(UnicodeType.Ain, (char) 1593, CharacterType.Ain);
        addItem(UnicodeType.Ghain, (char) 1594, CharacterType.Ghain);
        addItem(UnicodeType.Tatweel, (char) 1600, CharacterType.Tatweel);
        addItem(UnicodeType.Fa, (char) 1601, CharacterType.Fa);
        addItem(UnicodeType.Qaf, (char) 1602, CharacterType.Qaf);
        addItem(UnicodeType.Kaf, (char) 1603, CharacterType.Kaf);
        addItem(UnicodeType.Lam, (char) 1604, CharacterType.Lam);
        addItem(UnicodeType.Meem, (char) 1605, CharacterType.Meem);
        addItem(UnicodeType.Noon, (char) 1606, CharacterType.Noon);
        addItem(UnicodeType.Ha, (char) 1607, CharacterType.Ha);
        addItem(UnicodeType.Waw, (char) 1608, CharacterType.Waw);
        addItem(UnicodeType.AlifMaksura, (char) 1609, CharacterType.AlifMaksura);
        addItem(UnicodeType.Ya, (char) 1610, CharacterType.Ya);
        addItem(UnicodeType.Fathatan, (char) 1611, DiacriticType.Fathatan);
        addItem(UnicodeType.Dammatan, (char) 1612, DiacriticType.Dammatan);
        addItem(UnicodeType.Kasratan, (char) 1613, DiacriticType.Kasratan);
        addItem(UnicodeType.Fatha, (char) 1614, DiacriticType.Fatha);
        addItem(UnicodeType.Damma, (char) 1615, DiacriticType.Damma);
        addItem(UnicodeType.Kasra, (char) 1616, DiacriticType.Kasra);
        addItem(UnicodeType.Shadda, (char) 1617, DiacriticType.Shadda);
        addItem(UnicodeType.Sukun, (char) 1618, DiacriticType.Sukun);
        addItem(UnicodeType.Maddah, (char) 1619, DiacriticType.Maddah);
        addItem(UnicodeType.HamzaAbove, (char) 1620, DiacriticType.HamzaAbove);
        addItem(UnicodeType.AlifKhanjareeya, (char) 1648, null, DiacriticType.AlifKhanjareeya);
        addItem(UnicodeType.AlifWithHamzatWasl, (char) 1649, CharacterType.Alif, DiacriticType.HamzatWasl);
        addItem(UnicodeType.SmallHighSeen, (char) 1756, CharacterType.SmallHighSeen);
        addItem(UnicodeType.SmallHighRoundedZero, (char) 1759, CharacterType.SmallHighRoundedZero);
        addItem(UnicodeType.SmallHighUprightRectangularZero, (char) 1760, CharacterType.SmallHighUprightRectangularZero);
        addItem(UnicodeType.SmallHighMeemIsolatedForm, (char) 1762, CharacterType.SmallHighMeemIsolatedForm);
        addItem(UnicodeType.SmallLowSeen, (char) 1763, CharacterType.SmallLowSeen);
        addItem(UnicodeType.SmallWaw, (char) 1765, CharacterType.SmallWaw);
        addItem(UnicodeType.SmallYa, (char) 1766, CharacterType.SmallYa);
        addItem(UnicodeType.SmallHighNoon, (char) 1768, CharacterType.SmallHighNoon);
        addItem(UnicodeType.EmptyCentreLowStop, (char) 1770, CharacterType.EmptyCentreLowStop);
        addItem(UnicodeType.EmptyCentreHighStop, (char) 1771, CharacterType.EmptyCentreHighStop);
        addItem(UnicodeType.RoundedHighStopWithFilledCentre, (char) 1772, CharacterType.RoundedHighStopWithFilledCentre);
        addItem(UnicodeType.SmallLowMeem, (char) 1773, CharacterType.SmallLowMeem);
    }
}