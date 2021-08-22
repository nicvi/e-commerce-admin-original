package sdcn.project.ecommerce_admin_original;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

public class BillComparator extends DiffUtil.ItemCallback<Bill> {
    @Override
    public boolean areItemsTheSame(@NonNull Bill oldItem, @NonNull Bill newItem) {
        return oldItem.getBillID().equals(newItem.getBillID());
    }

    @Override
    public boolean areContentsTheSame(@NonNull Bill oldItem, @NonNull Bill newItem) {
        return oldItem.getBillID().equals(newItem.getBillID());
    }
}