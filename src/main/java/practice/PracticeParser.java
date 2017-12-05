package practice;

import session.Practice;

import java.util.LinkedList;
import java.util.Optional;

/**
 *  Verarbeitet eine Bot-Message, die als Übungsbeschreibung erkannt wurde zu Wortbestandteilen.
 *
 *  <Einfache Implementierung zu Testzwecken>
 */
public class PracticeParser
{
    /**
     * <Bezeichnung> <Anzahl> [<Wiederholungen>]
     */
    public PracticeParseResult parseToPractice(LinkedList<String> tokens)
    {
        if(tokens.size() < 2)
        {
            return new PracticeParseResult(Optional.empty());
        }
        else
        {
            String title = tokens.get(0);
            int reps = Integer.parseInt(tokens.get(1));
            Practice practice = new Practice();
            practice.setReps(reps);
            practice.setTitle(title);
            return new PracticeParseResult(Optional.of(practice));
        }
    }
}
