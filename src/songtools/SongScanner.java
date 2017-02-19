package songtools;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import rhyme.LineRhymeScheme;
import rhyme.Phoneticizer;
import elements.*;
import rhyme.Rhyme;
import rhyme.WordsByRhyme;
import utils.U;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public abstract class SongScanner {

    public static InfoSong getTemplateSong(TextInFormat format, String fileString) {
        switch (format) {
            case NORMAL:
                return getTemplateSong(fileString);
            case PAUL:
                return getTemplateSongPaulFormat(fileString);
            default:
                return getTemplateSong(fileString);
        }
    }

    public static InfoSong getInfoSong(TextInFormat format, String fileString) {
        switch (format) {
            case NORMAL:
                return getInfoSong(fileString);
            case PAUL:
                return getInfoSongPaulFormat(fileString);
            default:
                return getInfoSong(fileString);
        }
    }



    public static InfoSong getTemplateSong(String fileString) {
        String rawTemplateText = readFileToText(fileString);
        rawTemplateText = SongMutator.cleanText(rawTemplateText);
//        rawTemplateText = SongMutator.personToPerson(rawTemplateText, Person.FIRST, Person.SECOND);
//        rawTemplateText = SongMutator.stringToString(rawTemplateText, "my", "your");
        List<Sentence> parsedSentences = U.getStanfordNlp().parseTextToSentences(rawTemplateText);
        setSyllablesForSentences(parsedSentences);
        InfoSong templateInfoSong = sentencesToInfoSong(rawTemplateText, parsedSentences);
        return templateInfoSong;
    }

    public static InfoSong getTemplateSongPaulFormat(String fileString) {
        String rawTemplateText = readFileToText(fileString);
        InfoSong song = readPaulFormat(rawTemplateText);
        List<Sentence> parsedSentences = U.getStanfordNlp().parseWordsToSentences(song.getAllWords());
        setSyllablesForSentences(parsedSentences);
        return song;
    }

    public static InfoSong readPaulFormat(String text) {
        String[] lines = text.split("\\n");
        InfoSong song = new InfoSong("title", "writer", "genre");
        Stanza currentStanza = null;
        for (String lineStr : lines) {
            if (lineStr.matches("TITLE: \\w+")) {
                song.setTitle(lineStr.replace("TITLE: ", ""));
            }
            else if (lineStr.matches("INTRO") ||
                    lineStr.matches("VERSE") ||
                    lineStr.matches("CHORUS") ||
                    lineStr.matches("BRIDGE")) {
                if (currentStanza != null)
                    song.add(currentStanza);
                currentStanza = new Stanza();
            }
            else if (lineStr.matches("[A-Z]\\t.+")) {
                String rhyme = Character.toString(lineStr.charAt(0));
                Line currentLine = new Line();
                String[] words = lineStr.split(" ");
                for (String word : words) {
                    Word tempWord = new Word(word);
                    currentLine.add(tempWord);
                }
                currentStanza.add(currentLine);
            }
        }
        return song;
    }


    public static InfoSong getInfoSong(String fileString) {
        InfoSong templateInfoSong = getTemplateSong(fileString);

        String[] fileNameStrs = fileString.split("(@)|(.lyrics.txt)|( - )");
        if (fileNameStrs.length == 2) {
            String writer = fileNameStrs[0];
            String title = fileNameStrs[1];
            templateInfoSong.setTitle(title);
            templateInfoSong.setWriter(writer);
            templateInfoSong.setGenre("pop");
            templateInfoSong.setProgrammer("Ben Bay");
        }

        return templateInfoSong;
    }

    public static InfoSong getInfoSongPaulFormat(String fileString) {
        InfoSong templateInfoSong = getTemplateSongPaulFormat(fileString);

        String[] fileNameStrs = fileString.split("(@)|(.lyrics.txt)|( - )");
        if (fileNameStrs.length == 2) {
            String writer = fileNameStrs[0];
            String title = fileNameStrs[1];
            templateInfoSong.setTitle(title);
            templateInfoSong.setWriter(writer);
            templateInfoSong.setGenre("pop");
            templateInfoSong.setProgrammer("Ben Bay");
        }

        return templateInfoSong;
    }

    public static String readFileToText(String fileString) {
        U.testPrintln("Entering readSong");
        Song infoSong = new Song();
        try {
            File file = new File(U.rootPath + "data/songs/dev-template-songs/" + fileString);
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();

            return new String(data, "UTF-8");
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setSyllablesForSentences(List<Sentence> sentences) {
        for (Sentence sentence : sentences) {
            for (Word word : sentence) {
                word.setSyllables(Phoneticizer.getSyllablesForWord(word));
            }
        }
    }

    public static InfoSong sentencesToInfoSong(String rawSong, List<Sentence> parsedSentences) {
        //TODO > Include in InfoSong a CoreMap for each sentence.

        InfoSong tempInfoSong = new InfoSong("", "", "");

        ArrayList<String> rawStanzas = new ArrayList<String>(Arrays.asList(rawSong.split("\\n\\n")));

        ArrayList<ArrayList<char[]>> punctuation = new ArrayList<ArrayList<char[]>>();
        Sentence currentStringSentence = new Sentence();

        int currentSentenceIndex = 0;

        for (int i = 0; i < rawStanzas.size(); i++) {
            ArrayList<String> rawLines = new ArrayList<>(Arrays.asList(rawStanzas.get(i).split("\\n")));
            Stanza tempStanza = new Stanza();
            ArrayList<char[]> punctStanza = new ArrayList<>();
            int sentenceWordIndex = 0;
            for (int j = 0; j < rawLines.size(); j++) {
                String rawLine = rawLines.get(j);
                //kill punctuation (for now)
                //TODO do something about appostrophes
                rawLine = rawLine.replaceAll("[^\\w\\d\\s]", "");
                Line tempLine = new Line();
                ArrayList<String> rawWords = new ArrayList<>(Arrays.asList(rawLine.split("\\s")));
                for (int k = 0; k < rawWords.size(); k++) {

                    // if current sentence is the instanceSpecific as the parsed sentence
                    if (currentStringSentence.toString().equals(parsedSentences.get(currentSentenceIndex).toString().replaceAll("[^\\w\\d\\s]", ""))) {
                        currentStringSentence = new Sentence();
                        sentenceWordIndex = 0;
                        currentSentenceIndex++;
                    }

                    // add a word to the current sentence
                    if (parsedSentences.size() > currentSentenceIndex - 1) {
                        tempLine.add(parsedSentences.get(currentSentenceIndex).get(sentenceWordIndex));
                        currentStringSentence.add(new Word(rawWords.get(k)));
                        sentenceWordIndex++;
                    }

                }
                tempStanza.add(tempLine);
            }
            tempInfoSong.add(tempStanza);
        }

        //TODO: eventually make this internal
        return tempInfoSong;
    }

    public static ArrayList<Sentence> stanfordSentencesToSentences(List<List<CoreLabel>> stanfordSentences) {
        ArrayList<Sentence> resultSentences = new ArrayList<Sentence>();
        for (List<CoreLabel> sentence : stanfordSentences) {
            Sentence resultSentence = new Sentence();
            for (CoreLabel token : sentence) {
                String spelling = token.get(CoreAnnotations.TextAnnotation.class);
                Word resultWord = new Word(spelling);
                resultWord.setPos(Pos.valueOf(token.get(CoreAnnotations.PartOfSpeechAnnotation.class)));
                resultWord.setNe(Ne.valueOf(token.get(CoreAnnotations.NamedEntityTagAnnotation.class)));
                resultSentence.add(resultWord);
            }
            resultSentences.add(resultSentence);
        }
        return resultSentences;
    }

    public static Set<Integer> getRandomIndexes(Set<Integer> originalIndexes, double replacement_frequency) {
        U.testPrintln("Entering getRandomIndexes");

//        //Make whiteListIndexes
//        List<Integer> whiteListIndexes = new ArrayList<Integer>();
//        for (int i = 0; i < originalIndexes.size(); i++)
//            whiteListIndexes.add(i);
//
//        //Make resultIndexes
//        HashSet<Integer> resultIndexes = new HashSet<Integer>();
//
//        //Remove blacklisted indexes from whiteListIndexes
//        for (int i = 0; i < originalIndexes.size(); i++) {
//            Word w = originalIndexes.get(i);
//            Pos wordsToPos = w.getParts();
//            String spelling = w.getLowerSpelling().toLowerCase();
//            if (
////                    spelling.equals("it's") ||
////                    spelling.equals("i'm") ||
////                    spelling.equals("i've") ||
////                    spelling.equals("i'll") ||
////                    spelling.equals("you're") ||
////                    spelling.equals("you've") ||
////                    spelling.equals("you'll") ||
////                    spelling.equals("we're") ||
////                    spelling.equals("we've") ||
////                    spelling.equals("we'll") ||
////                    spelling.equals("they're") ||
////                    spelling.equals("they'll") ||
////                    spelling.equals("they've") ||
////                    spelling.equals("he's") ||
////                    spelling.equals("he'll") ||
////                    spelling.equals("she's") ||
////                    spelling.equals("she'll") ||
////                    spelling.equals("don't") ||
////                    spelling.equals("won't") ||
////                    spelling.equals("doesn't") ||
////                    spelling.equals("hasn't") ||
////                    spelling.equals("haven't") ||
//
//                    // manageable parts of speech
//                    (       wordsToPos != Pos.CD &&
//                            wordsToPos != Pos.JJ &&
//                            wordsToPos != Pos.JJR &&
//                            wordsToPos != Pos.JJS &&
//                            wordsToPos != Pos.NN &&
//                            wordsToPos != Pos.NNS &&
//                            wordsToPos != Pos.NNP &&
//                            wordsToPos != Pos.NNPS &&
//                            wordsToPos != Pos.RB &&
//                            wordsToPos != Pos.RBR &&
//                            wordsToPos != Pos.RBS &&
//                            wordsToPos != Pos.UH
//
////                            &&
////                            wordsToPos != Pos.VB &&
////                            wordsToPos != Pos.VBD &&
////                            wordsToPos != Pos.VBG &&
////                            wordsToPos != Pos.VBN &&
////                            wordsToPos != Pos.VBP &&
////                            wordsToPos != Pos.VBZ
//
//                            // tricky parts of speech
////                    wordsToPos == Pos.TO ||
////                    wordsToPos == Pos.IN ||
////                    wordsToPos == Pos.PRP ||
////                    wordsToPos == Pos.WRB ||
////                    wordsToPos == Pos.CC ||
////                    wordsToPos == Pos.DT ||
////                    wordsToPos == Pos.EX ||
////                    wordsToPos == Pos.EX ||
////                    wordsToPos == Pos.PRP$ ||
////                    wordsToPos == Pos.UNKNOWN
//                    )
//                    )
//                whiteListIndexes.remove(i);
//        }
        //if it's 100% replacement, just return all non-blacklisted indexes
        if (replacement_frequency == 1 || replacement_frequency > 1)
            return originalIndexes;

        if (originalIndexes.size() < 1)
            return originalIndexes;

        HashSet<Integer> randomIndexes = new HashSet<Integer>();
        int nOfOriginalIndexes = originalIndexes.size();
        int num_to_replace = (int)(replacement_frequency * nOfOriginalIndexes); //TODO decide which way to round
        Random rand = new Random();
        int index_to_add = rand.nextInt(nOfOriginalIndexes);
        while (num_to_replace > 0) {
            while(randomIndexes.contains(index_to_add))
                index_to_add = rand.nextInt(nOfOriginalIndexes);
            randomIndexes.add(index_to_add);
            num_to_replace--;
        }
        return randomIndexes;
    }

    public static Set<PositionedWord> getPositionedWords(Song infoSong) {
        Set<PositionedWord> positionedWords = new HashSet<>();
        for (int s = 0; s < infoSong.getStanzas().size(); s++) {
            Stanza stanza = infoSong.getStanzas().get(s);
            for (int l = 0; l < stanza.getLines().size(); l++) {
                Line line = stanza.getLines().get(l);
                for (int w = 0; w < line.getWords().size(); w++) {
                    Word word = line.getWords().get(w);
                    positionedWords.add(new PositionedWord(word, s, l , w));
                }

            }

        }
        return positionedWords;
    }

    public static WordsByRhyme getRhymeSchemeWords(Song infoSongToMark, LineRhymeScheme rhymeScheme) {
        if (infoSongToMark != null && !infoSongToMark.getAllWords().isEmpty()) {
            List<SongElement> lines = infoSongToMark.getAllSubElementsOfType(new Line());
            WordsByRhyme wordsByRhyme = new WordsByRhyme();
            for (int l = 0; l < lines.size(); l++) {
                Line line = (Line)lines.get(l);
                if (rhymeScheme.contains(l)) {
                    Word word = line.getAllWords().get(line.getSize() - 1);
                    Rhyme rhyme = rhymeScheme.getRhymeByIndex(l);
                    wordsByRhyme.putWord(rhyme, word);
                }
            }
            return wordsByRhyme;
        }
        return null;
    }

    public static int getNLines(Song infoSong) {
        List<SongElement> lines = infoSong.getAllSubElementsOfType(new Line());
        return lines.size();
    }

}














































