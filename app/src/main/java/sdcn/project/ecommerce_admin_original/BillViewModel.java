package sdcn.project.ecommerce_admin_original;


import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.rxjava2.PagingRx;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import io.reactivex.Flowable;
import kotlinx.coroutines.CoroutineScope;

// TODO if you have an "error: cannot find symbol" while trying to execute a Kotlin class in a Java
//  project It looks like your kotlin code isn't compiled during build process.
//  I have this problem trying to call a "FirestorePagingSource" object, that was defined in
//  a kotlin class, from the java class "BillViewModel".
public class BillViewModel extends ViewModel {
    // Define Flowable for movies
    public Flowable<PagingData<Bill>> pagingDataFlow;
    private String strPickedUp;

    public BillViewModel(String strPickedUp) {
        this.strPickedUp = strPickedUp;
        init();
    }

    // Init ViewModel Data
    private void init() {
        // Define Paging Source
        FirestorePagingSource moviePagingSource = new FirestorePagingSource(
                FirebaseFirestore.getInstance(),strPickedUp
        );

        System.out.println("moviePagingSource.toString(): "+ moviePagingSource.toString());


        // Create new Pager
        Pager<QuerySnapshot, Bill> pager = new Pager(
                // Create new paging config
                new PagingConfig(10, // pageSize - Count of items in one page
                        10, // prefetchDistance - Number of items to prefetch
                        false, // enablePlaceholders - Enable placeholders for data which is not yet loaded
                        10, // initialLoadSize - Count of items to be loaded initially
                        10 * 499),// maxSize - Count of total items to be shown in recyclerview
                () -> moviePagingSource); // set paging source

        // inti Flowable
        pagingDataFlow = PagingRx.getFlowable(pager);

        //pagingDataFlow.isEmpty();
        System.out.println("pagingDataFlow.isEmpty(): "+ pagingDataFlow.isEmpty());
        CoroutineScope coroutineScope = ViewModelKt.getViewModelScope(this);
        PagingRx.cachedIn(pagingDataFlow, coroutineScope);

    }
}