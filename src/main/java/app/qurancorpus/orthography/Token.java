package app.qurancorpus.orthography;

import app.qurancorpus.arabic.ArabicText;
import memseqdb.GraphNode;

public record Token(Location location, ArabicText arabicText) implements GraphNode {
}