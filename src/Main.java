import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.trees.Dependency;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TypedDependency;
import back.StnPrsrController;
import back.TwitterController;

public class Main {
	
	private static TwitterController twtcntrl;
	private static StnPrsrController stnprsr;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		twtcntrl = new TwitterController();
		stnprsr = new StnPrsrController();
		//String test = "drumpf";
		
		//twtcntrl.searchTweetsWith(test);
		
		Tree parsedTree = stnprsr.parseString("A very good car");
		
		
		Collection<TypedDependency> dependencies = stnprsr.findDependencies(parsedTree);
		
		Vector<String[]> returned = adjectivalModifier(dependencies);
		
		for(String[] results : returned)
		{
			System.out.println(results[0] +" - "+ results[1]+"\n");
		}
		//System.out.println(dependencies);
	}
	
	
	
	//ACHANDO AJETIVOS//
	
	private static Vector<String[]> adjectivalModifier(Collection<TypedDependency> dependencies)
	{
		Vector<String[]> returnedFound = new Vector<String[]>();
		
		for(TypedDependency td:dependencies)
		{
			if(td.reln().toString().equals("amod")){
				String[] tempRelation = new String[2];
				tempRelation[0] = td.gov().word();
				tempRelation[1] = td.dep().word();
				returnedFound.add(tempRelation);
			}
		}
		return returnedFound;		
	}
	
	private static void identifyAdjectiveModifier(Tree parsedTree)
	{
		
		List<Tree> leaves = parsedTree.getLeaves();
        // Print words and Pos Tags
        for (Tree leaf : leaves) { 
            Tree parent = leaf.parent(parsedTree);
//            System.out.print(leaf.label().value() + "-" + parent.label().value() + " \n");
        }
	}
	
}
