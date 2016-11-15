package uncoma.termSeparation;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.POS;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by aderenzis on 14/11/16.
 */
public class TermSeparator {

    private IDictionary dictionary = null;
    //			static String path = "C:\\Program Files (x86)\\WordNet\\2.1\\dict";
//			static String path = java.lang.System.getenv("WNSEARCHDIR");
    static String path = System.getenv("WNHOME")+ File.separator+"dict"+File.separator;
    static URL url = null;


    public  boolean initializeDictionary(){
        try {
            url = new URL("file", "localhost", path);
        } catch (MalformedURLException e) {
        }
        if (url == null)
            return false;
        this.dictionary = new Dictionary(url);
        try {
            dictionary.open();
            return true;
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return false;
        }
    }

    public  boolean isWord (String wordQuery)
    {
        IIndexWord idxWord = dictionary.getIndexWord(wordQuery, POS.NOUN);
        if (idxWord == null)
            idxWord = dictionary.getIndexWord(wordQuery, POS.VERB);
        if (idxWord == null)
            idxWord = dictionary.getIndexWord(wordQuery, POS.ADJECTIVE);
        if (idxWord == null)
            idxWord = dictionary.getIndexWord(wordQuery, POS.ADVERB);
        if (idxWord == null)
            return false;
        else
            return true;

    }

    public  ArrayList<String> separateTerms(String term)
    {
        if(dictionary==null || !dictionary.isOpen()){
            initializeDictionary();
        }
        ArrayList<String> returnedTerms = new ArrayList<String>();
        if(term.length()>0)
        {
            boolean mayus =false;
            String ret="";
            String retMayus="";
            char lastMayus=0;
            char charAux;
            if(term.charAt(0)>=65 && term.charAt(0)<=90) // Si es mayuscula la 1er letra
            {
                charAux= (char) (term.charAt(0)+32); // guarda la minuscula
                ret=Character.toString(charAux); // ret almaceno la letra
                retMayus=Character.toString(charAux); // retMayus almaceno
                mayus=true;
            }
            else
                ret=Character.toString(term.charAt(0)); // si no es mayuscula se almacena el char en ret
            for(int i=1;i< term.length();i++)
            {
                String auxiliar = Character.toString(term.charAt(i));
                if(term.charAt(i)>=65 && term.charAt(i)<=90) // Si es una mayuscula
                {
                    //if(ret.length()>0 || retMayus.length()>0)

                    if(!mayus) //Es la primer Mayuscula
                    {
                        if(retMayus.length()>1) // Ya existia anteriormente una seguidilla de mayusculas
                        {
                            if(isWord(lastMayus+ret))//es una palabra la ultima mayuscula + minusculas
                            {
                                returnedTerms.add(retMayus.substring(0, retMayus.length()-1));
                                returnedTerms.add(lastMayus+ret);
                                lastMayus=0;
                                retMayus="";
                                ret="";
                            }
                            else
                            {
                                returnedTerms.add(retMayus);
                                returnedTerms.add(ret);
                                lastMayus=0;
                                retMayus="";
                                ret="";
                            }
                        }
                        else // No existia anteriormente una seguidilla de mayusculas
                            if(ret.length()>0)
                                returnedTerms.add(ret);

                        mayus=true;
                        charAux= (char) (term.charAt(i)+32);
                        ret=Character.toString(charAux);
                        retMayus=Character.toString(charAux);
                    }
                    else //No es la primer mayuscula consecutiva
                    {
                        charAux= (char) (term.charAt(i)+32);
                        retMayus = retMayus+charAux;
                        ret="";
                    }


                }
                else //No es una Mayuscula
                {
                    if(term.charAt(i) == 45 || term.charAt(i)== 95 || esNumero(term.charAt(i))) //  Si es _ o -
                    {
                        if(ret.length()>0) // si el guion esta despues de una acumulacion de Minusculas
                        {
                            returnedTerms.add(ret);
                            ret="";
                            retMayus="";
                        }
                        else if(retMayus.length()>0) // si el guion esta despues de una acumulacion de Mayusculas
                        {
                            returnedTerms.add(retMayus);
                            retMayus="";
                        }

                        mayus=false;
                    } // No es mayuscula ni _ ni - ni Numero// es una letra minuscula
                    else
                    {
                        if(mayus) // la Letra anterior era una mayuscula
                        {
                            lastMayus= (char) (term.charAt(i-1)+32);
                            ret=ret+term.charAt(i);
                            mayus=false;
                        }
                        else // la letra anterior no era mayuscula
                        {
                            ret=ret+term.charAt(i);
                        }

                    }
                }
            }
            if(ret.length()>0 | retMayus.length()>1)
            {
                if(retMayus.length()>1) // Ya existia anteriormente una seguidilla de mayusculas
                {
                    if(lastMayus != 0 && ret.length()>0 && isWord(lastMayus+ret)) // Es un && porque si lastMayus es 0 no debe entrar al metodo isWord.
                    {
                        returnedTerms.add(retMayus.substring(0, retMayus.length()-1));
                        returnedTerms.add(lastMayus+ret);
                        lastMayus=0;
                        retMayus="";
                        ret="";
                    }
                    else
                    {
                        if(retMayus.length()>1);
                        returnedTerms.add(retMayus);
                        if(ret.length()>0)
                            returnedTerms.add(ret);
                        lastMayus=0;
                        retMayus="";
                        ret="";
                    }
                }
                else
                    returnedTerms.add(ret);
            }
        }
        return returnedTerms;
    }

    private static boolean esNumero(char charAt) {

        return (48<=charAt && charAt<=57);
    }

    public TermSeparator(){
        initializeDictionary();
    }
}
