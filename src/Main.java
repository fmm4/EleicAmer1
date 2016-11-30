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
	private static AspectExtractor ae;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		twtcntrl = new TwitterController();
		stnprsr = new StnPrsrController();
		//String test = "drumpf";
		
		//twtcntrl.searchTweetsWith(test);
		
		Tree parsedTree = stnprsr.parseString("The plot could have been better.");
		
		
		Collection<TypedDependency> dependencies = stnprsr.findDependencies(parsedTree);
		
		Vector<String[]> returned = ae.extractAspects(dependencies);		
		
		for(String[] results : returned)
		{
			System.out.println(results[0] +" - "+ results[1]+"\n");
		}
		System.out.println(dependencies);
	}
	
	

	
}
