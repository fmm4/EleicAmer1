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
import back.WS4J;

public class Main {
	
	private static TwitterController twtcntrl;
	private static StnPrsrController stnprsr;
	private static AspectExtractor ae;
	private static WS4J ws;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		twtcntrl = new TwitterController();
		stnprsr = new StnPrsrController();
		//String test = "drumpf";
		
		//twtcntrl.searchTweetsWith(test);
		
		Tree parsedTree = stnprsr.parseString("The movie acting was not very good");
		
		
		Collection<TypedDependency> dependencies = stnprsr.findDependencies(parsedTree);
		

		
		Vector<String[]> returned = ae.extractAspects(dependencies);		
		
		for(String[] results : returned)
		{
			System.out.println(stnprsr.tokenizeString(results[1]) +" "+ stnprsr.tokenizeString(results[0])+"");
		}
		
		System.out.println(returned.elementAt(0)[1]+" "+returned.elementAt(0)[0]+" , "+returned.elementAt(1)[1]+" "+returned.elementAt(1)[0]);
	//	double k = ws.runJCN(returned.elementAt(0)[0],returned.elementAt(1)[0]);
	//	double g = ws.runJCN(returned.elementAt(0)[1],returned.elementAt(1)[1]);
//		
//		System.out.println("Score: "+k);
//		System.out.println("Score: "+g);

		for(TypedDependency dp:dependencies){
			ae.debugDependency(dp);
		}
	}
	
	

	
}
