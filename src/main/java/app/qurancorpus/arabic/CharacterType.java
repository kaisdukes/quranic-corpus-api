package app.qurancorpus.arabic;

public enum CharacterType {
    Alif("alif"),
    Ba("bā"),
    Ta("tā"),
    Tha("thā"),
    Jeem("jīm"),
    HHa("ḥā"),
    Kha("khā"),
    Dal("dāl"),
    Thal("dhāl"),
    Ra("rā"),
    Zain("zāy"),
    Seen("sīn"),
    Sheen("shīn"),
    Sad("ṣād"),
    DDad("ḍād"),
    TTa("ṭā"),
    DTha("ẓā"),
    Ain("ʿayn"),
    Ghain("ghayn"),
    Fa("fā"),
    Qaf("qāf"),
    Kaf("kāf"),
    Lam("lām"),
    Meem("mīm"),
    Noon("nūn"),
    Ha("hā"),
    Waw("wāw"),
    Ya("yā"),
    Hamza("hamza"),
    AlifMaksura,
    TaMarbuta,
    Tatweel,
    SmallHighSeen,
    SmallHighRoundedZero,
    SmallHighUprightRectangularZero,
    SmallHighMeemIsolatedForm,
    SmallLowSeen,
    SmallWaw,
    SmallYa,
    SmallHighNoon,
    EmptyCentreLowStop,
    EmptyCentreHighStop,
    RoundedHighStopWithFilledCentre,
    SmallLowMeem,
    Placeholder;

    public static final CharacterType[] CHARACTER_TYPES = values();

    public String getPhoneticName() {
        return phoneticName;
    }

    public String getPhoneticRoot() {
        return this == Alif ? Hamza.phoneticName : phoneticName;
    }

    CharacterType() {
    }

    CharacterType(String phoneticName) {
        this.phoneticName = phoneticName;
    }

    private String phoneticName;
}