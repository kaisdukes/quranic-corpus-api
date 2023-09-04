package app.qurancorpus.arabic.encoding.buckwalter;

import app.qurancorpus.arabic.CharacterType;
import app.qurancorpus.arabic.DiacriticType;
import app.qurancorpus.arabic.encoding.EncodingTableBase;
import app.qurancorpus.arabic.encoding.unicode.UnicodeType;

public class BuckwalterTable extends EncodingTableBase {
	public static final BuckwalterTable BUCKWALTER_TABLE = new BuckwalterTable();

	private BuckwalterTable() {
		addItem(UnicodeType.Hamza, '\'', CharacterType.Hamza);
		addItem(UnicodeType.AlifWithHamzaAbove, '>', CharacterType.Alif, DiacriticType.HamzaAbove);
		addItem(UnicodeType.WawWithHamzaAbove, '&', CharacterType.Waw, DiacriticType.HamzaAbove);
		addItem(UnicodeType.AlifWithHamzaBelow, '<', CharacterType.Alif, DiacriticType.HamzaBelow);
		addItem(UnicodeType.YaWithHamzaAbove, '}', CharacterType.Ya, DiacriticType.HamzaAbove);
		addItem(UnicodeType.Alif, 'A', CharacterType.Alif);
		addItem(UnicodeType.Ba, 'b', CharacterType.Ba);
		addItem(UnicodeType.TaMarbuta, 'p', CharacterType.TaMarbuta);
		addItem(UnicodeType.Ta, 't', CharacterType.Ta);
		addItem(UnicodeType.Tha, 'v', CharacterType.Tha);
		addItem(UnicodeType.Jeem, 'j', CharacterType.Jeem);
		addItem(UnicodeType.HHa, 'H', CharacterType.HHa);
		addItem(UnicodeType.Kha, 'x', CharacterType.Kha);
		addItem(UnicodeType.Dal, 'd', CharacterType.Dal);
		addItem(UnicodeType.Thal, '*', CharacterType.Thal);
		addItem(UnicodeType.Ra, 'r', CharacterType.Ra);
		addItem(UnicodeType.Zain, 'z', CharacterType.Zain);
		addItem(UnicodeType.Seen, 's', CharacterType.Seen);
		addItem(UnicodeType.Sheen, '$', CharacterType.Sheen);
		addItem(UnicodeType.Sad, 'S', CharacterType.Sad);
		addItem(UnicodeType.DDad, 'D', CharacterType.DDad);
		addItem(UnicodeType.TTa, 'T', CharacterType.TTa);
		addItem(UnicodeType.DTha, 'Z', CharacterType.DTha);
		addItem(UnicodeType.Ain, 'E', CharacterType.Ain);
		addItem(UnicodeType.Ghain, 'g', CharacterType.Ghain);
		addItem(UnicodeType.Tatweel, '_', CharacterType.Tatweel);
		addItem(UnicodeType.Fa, 'f', CharacterType.Fa);
		addItem(UnicodeType.Qaf, 'q', CharacterType.Qaf);
		addItem(UnicodeType.Kaf, 'k', CharacterType.Kaf);
		addItem(UnicodeType.Lam, 'l', CharacterType.Lam);
		addItem(UnicodeType.Meem, 'm', CharacterType.Meem);
		addItem(UnicodeType.Noon, 'n', CharacterType.Noon);
		addItem(UnicodeType.Ha, 'h', CharacterType.Ha);
		addItem(UnicodeType.Waw, 'w', CharacterType.Waw);
		addItem(UnicodeType.AlifMaksura, 'Y', CharacterType.AlifMaksura);
		addItem(UnicodeType.Ya, 'y', CharacterType.Ya);
		addItem(UnicodeType.Fathatan, 'F', DiacriticType.Fathatan);
		addItem(UnicodeType.Dammatan, 'N', DiacriticType.Dammatan);
		addItem(UnicodeType.Kasratan, 'K', DiacriticType.Kasratan);
		addItem(UnicodeType.Fatha, 'a', DiacriticType.Fatha);
		addItem(UnicodeType.Damma, 'u', DiacriticType.Damma);
		addItem(UnicodeType.Kasra, 'i', DiacriticType.Kasra);
		addItem(UnicodeType.Shadda, '~', DiacriticType.Shadda);
		addItem(UnicodeType.Sukun, 'o', DiacriticType.Sukun);
		addItem(UnicodeType.Maddah, '^', DiacriticType.Maddah);
		addItem(UnicodeType.HamzaAbove, '#', DiacriticType.HamzaAbove);
		addItem(UnicodeType.AlifKhanjareeya, '`', null, DiacriticType.AlifKhanjareeya);
		addItem(UnicodeType.AlifWithHamzatWasl, '{', CharacterType.Alif, DiacriticType.HamzatWasl);
		addItem(UnicodeType.SmallHighSeen, ':', CharacterType.SmallHighSeen);
		addItem(UnicodeType.SmallHighRoundedZero, '@', CharacterType.SmallHighRoundedZero);
		addItem(UnicodeType.SmallHighUprightRectangularZero, '"', CharacterType.SmallHighUprightRectangularZero);
		addItem(UnicodeType.SmallHighMeemIsolatedForm, '[', CharacterType.SmallHighMeemIsolatedForm);
		addItem(UnicodeType.SmallLowSeen, ';', CharacterType.SmallLowSeen);
		addItem(UnicodeType.SmallWaw, ',', CharacterType.SmallWaw);
		addItem(UnicodeType.SmallYa, '.', CharacterType.SmallYa);
		addItem(UnicodeType.SmallHighNoon, '!', CharacterType.SmallHighNoon);
		addItem(UnicodeType.EmptyCentreLowStop, '-', CharacterType.EmptyCentreLowStop);
		addItem(UnicodeType.EmptyCentreHighStop, '+', CharacterType.EmptyCentreHighStop);
		addItem(UnicodeType.RoundedHighStopWithFilledCentre, '%', CharacterType.RoundedHighStopWithFilledCentre);
		addItem(UnicodeType.SmallLowMeem, ']', CharacterType.SmallLowMeem);
		addItem(UnicodeType.AlifWithMaddah, '|', CharacterType.Alif, DiacriticType.Maddah);
		addItem(null, '/', CharacterType.Placeholder);
	}
}