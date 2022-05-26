/*Team: Random1
  Team members: Niall Meagher - 20768511
                Nathan Mahady - 20522563
                Floriana Melania Munteanu - 20349023
*/

package Random1;

import model.Move;

import java.util.Collections;
import java.util.List;

public class Quicksort {
    public static  void quickSort(final List<Move> list, Random1Bot bot) {
        class QuickSort{ //We need submethods to call quickSort between two indices
            int partition(List<Move> list, int lo, int hi){
                int i = lo, j = hi+1;
                while(true){
                    //find item to left of pivot to swap
                    while(bot.compareMoves(list.get(++i), list.get(lo), bot.getBoard()) < 0)
                    {if(i==hi) break;}

                    //find item to right of pivot to swap
                    while(bot.compareMoves(list.get(lo), list.get(--j), bot.getBoard()) < 0)
                    {if(i==lo) break;}

                    //Check if pointers cross
                    if(i>=j) break;

                    Collections.swap(list, i, j);
                }

                Collections.swap(list, lo, j);

                return j;
            }

            void sort(List<Move> list, int lo, int hi){
                if(hi<=lo) return;
                int j = partition(list, lo, hi);
                sort(list, lo, j-1);
                sort(list, j+1, hi);
            }
        }
        QuickSort qs = new QuickSort();
        Collections.shuffle(list);
        qs.sort(list, 0, list.size()-1);
    }
}
