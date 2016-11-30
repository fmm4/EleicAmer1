package back;

import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.parser.ui.Parser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.Dependencies;
import edu.stanford.nlp.trees.DiskTreebank;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.LabeledScoredTreeFactory;
import edu.stanford.nlp.trees.NPTmpRetainingTreeNormalizer;
import edu.stanford.nlp.trees.PennTreeReader;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreePrint;
import edu.stanford.nlp.trees.TreeReader;
import edu.stanford.nlp.trees.TreeReaderFactory;
import edu.stanford.nlp.trees.Treebank;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;

public class StnPrsrController {
	
	private static String taggerPath;
	private static String modelPath;
	
	public StnPrsrController(){
		modelPath = DependencyParser.DEFAULT_MODEL;
	    taggerPath = "edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger";
	}
	
	private final static String PCG_MODEL = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";        

    private final static TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "invertible=true");

    private final LexicalizedParser parser = LexicalizedParser.loadModel(PCG_MODEL);

    public Tree parse(String str) {                
        List<CoreLabel> tokens = tokenize(str);
        Tree tree = parser.apply(tokens);
        return tree;
    }

    private List<CoreLabel> tokenize(String str) {
        Tokenizer<CoreLabel> tokenizer =
            tokenizerFactory.getTokenizer(
                new StringReader(str));    
        return tokenizer.tokenize();
    }
    
    public static Tree parseString(String stringToParse)
    {
    	StnPrsrController parser = new StnPrsrController(); 
        Tree tree = parser.parse(stringToParse); 

//        List<Tree> leaves = tree.getLeaves();
//        // Print words and Pos Tags
//        for (Tree leaf : leaves) { 
//            Tree parent = leaf.parent(tree);
//            System.out.print(leaf.label().value() + "-" + parent.label().value() + " ");
//        }
        
        
        return tree;
    }
    
    public static Collection<TypedDependency> findDependencies(Tree parsableTree)
    {
    	Treebank tb = new DiskTreebank(new TreeReaderFactory() {
            public TreeReader newTreeReader(Reader in) {
              return new PennTreeReader(in, new LabeledScoredTreeFactory(),
                                        new NPTmpRetainingTreeNormalizer());
            }});
        TreebankLanguagePack tlp = new PennTreebankLanguagePack();
        GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
        TreePrint tp = new TreePrint("typedDependenciesCollapsed");
        
       
          GrammaticalStructure gs = gsf.newGrammaticalStructure(parsableTree);
          return gs.typedDependenciesCollapsed();
    }
    
    
    public static String tokenizeString(String stringToToken){

    		
    		List<CoreLabel> tokens_words = tokenizerFactory.getTokenizer(new StringReader(stringToToken)).tokenize();
    		
    		String retornado = "";
    		for(CoreLabel e:tokens_words)
    		{
    			retornado += e.word()+" ";
    		}
    		return retornado;   	 
    }
}
