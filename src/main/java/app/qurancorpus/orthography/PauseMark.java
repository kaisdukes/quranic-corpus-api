package app.qurancorpus.orthography;

public enum PauseMark {
    Compulsory,
    NotPermissible,
    ContinuationPreferred,
    PausePreferred,
    Permissible,
    Interchangeable;

    public static final PauseMark[] PAUSE_MARKS = values();
}