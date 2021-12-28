package pixer415.BRBoot;
import java.util.List;
import de.jilocasin.nearestneighbour.kdtree.KdTree;
//Allows multi-threading for partitioning "coast" pixels.
public class MultiExecutor implements Runnable {

	private List<Integer> dList;
	private KdTree<Integer> ls;
	private List<Pixel> originalLS;
    private int i;
    private Region r;
    public MultiExecutor(List<Integer> dList, int i, KdTree<Integer> xbox, List<Pixel> originalLS, Region r) {
        super();
        this.dList = dList;
        this.ls = xbox;
        this.originalLS = originalLS;
        this.i = i;
        this.r = r;
    }
    
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Main.nullKill(dList.get(i * 2), dList.get((i * 2) + 1), ls, originalLS, r);
	}
}
