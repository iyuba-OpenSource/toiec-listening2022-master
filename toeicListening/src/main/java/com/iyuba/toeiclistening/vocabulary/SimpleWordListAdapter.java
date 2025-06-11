package com.iyuba.toeiclistening.vocabulary;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.toeiclistening.R;
import com.iyuba.toeiclistening.entity.RememberWord;

import java.util.ArrayList;
import java.util.List;

public class SimpleWordListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements SectionIndexer {

    private int ITEM_TITLE = -1;
    private List<ViewHeader> headers = new ArrayList<>();

    Context context;
    boolean isTotal;
    List<RememberWord> cetRootWords;
    List<Object> dataSource;


    boolean showOrder;
    private SparseIntArray positionOfSection;
    private SparseIntArray sectionOfPosition;
    private ArrayList<Object> list;

    public SimpleWordListAdapter(List<RememberWord> cetRootWords, boolean isTotal) {
        this.cetRootWords = cetRootWords;
        this.isTotal = isTotal;
        dataSource = new ArrayList<>();
        dataSource.addAll(cetRootWords);
    }


    public List<RememberWord> getCetRootWords() {
        return cetRootWords;
    }

    public void setCetRootWords(List<RememberWord> cetRootWords) {
        this.cetRootWords = cetRootWords;
    }

    public void setData(List<RememberWord> cetRootWords) {
        this.cetRootWords = cetRootWords;
    }

    public void setShowOrder(boolean showOrder) {
        this.showOrder = showOrder;
        String s = "";
        for (int i = 0; i < dataSource.size(); i++) {
            if (!s.equals(((RememberWord) dataSource.get(i)).word.substring(0, 1).toUpperCase())) {
                if ((((RememberWord) dataSource.get(i)).word.substring(0, 1).toUpperCase()).equals("(")) {
                    s = "W";
                } else {
                    s = ((RememberWord) dataSource.get(i)).word.substring(0, 1).toUpperCase();
                }
                ViewHeader header = new ViewHeader(s, i);
                headers.add(header);
            }
        }

        int count = 0;
        for (ViewHeader header : headers) {
            header.setIndex(header.getIndex() + count);
            dataSource.add(header.getIndex(), header);
            count++;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        context = viewGroup.getContext();
        View view;
        if (viewType == -1) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.simple_word_header, viewGroup, false);
            return new HeaderView(view);
        } else {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.simple_word_item, viewGroup, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int pos) {

        if (viewHolder instanceof HeaderView) {
            ((HeaderView) viewHolder).header.setText(((ViewHeader) dataSource.get(pos)).getHeader());
            ((HeaderView) viewHolder).vHeader = (ViewHeader) dataSource.get(pos);
        } else {
            RememberWord rootWord = (RememberWord) dataSource.get(pos);
            ((ViewHolder) viewHolder).setItem(rootWord, pos);
        }

    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }

    @Override
    public Object[] getSections() {
        positionOfSection = new SparseIntArray();
        sectionOfPosition = new SparseIntArray();
        int count = getItemCount();
        list = new ArrayList<>();
        if (count > 0) {
            list.add("↑");
            positionOfSection.put(0, 0);
            sectionOfPosition.put(0, 0);
            for (int i = 0; i < count; i++) {
                String letter = " ";
                if (dataSource.get(i) instanceof RememberWord) {
                    letter = ((RememberWord) dataSource.get(i)).word.substring(0, 1).toUpperCase();

                } else if (dataSource.get(i) instanceof HeaderView) {
                    letter = ((ViewHeader) dataSource.get(i)).getHeader();
                }
                int section = list.size() - 1;
                if (list.get(section) != null && !list.get(section).equals(letter)) {
                    list.add(letter);
                    section++;
                    positionOfSection.put(section, i);
                }
                sectionOfPosition.put(i, section);
            }
        }
        return list.toArray(new String[list.size()]);
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return positionOfSection.get(sectionIndex);
    }

    @Override
    public int getSectionForPosition(int position) {
        return sectionOfPosition.get(position);
    }


    public class HeaderView extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView header;

        ViewHeader vHeader;

        public HeaderView(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            header = itemView.findViewById(R.id.header);
        }


        @Override
        public void onClick(View v) {
            switch (vHeader.getHeader()) {

            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView word;
        TextView pron;
        TextView def;
        ImageView info_img;

        RememberWord cetRootWord;

        boolean isChecked = false;
        boolean shouldShow = true;

        ObjectAnimator animator;
        ObjectAnimator animator_1;
        int positionItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            word = itemView.findViewById(R.id.word);
            pron = itemView.findViewById(R.id.pron);
            def = itemView.findViewById(R.id.def);
            info_img = itemView.findViewById(R.id.info_img);

            itemView.setOnClickListener(this);
            info_img.setOnClickListener(this);
            animator = ObjectAnimator.ofFloat(itemView, "alpha", 0f, 1f);
            animator_1 = ObjectAnimator.ofFloat(pron, "alpha", 0f, 1f);
            animator.setDuration(1000);
            animator_1.setDuration(1000);

        }

        public void setItem(RememberWord rootWord, int position) {
            word.setText(rootWord.word);
//            if (!rootWord.pron.startsWith("[")){
//                pron.setText(String.format("[%s]", TextAttr.decode(rootWord.pron)));
//            }else {
//                pron.setText(String.format("%s", TextAttr.decode(rootWord.pron)));
//            }
            positionItem = position;
            pron.setText(rootWord.pron);
            def.setText(rootWord.explain);
            setItemVisible();
            itemView.setTag(position);
            cetRootWord = rootWord;
            if (isTotal) {
                if (cetRootWord.statues == 2) {
                    word.setTextColor(context.getResources().getColor(R.color.green));
                } else if (cetRootWord.statues == 1) {
                    word.setTextColor(context.getResources().getColor(R.color.red));
                }
            }
        }


        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.info_img:
                    if (cetRootWords.size() > 100) {
                        WordStudyActivity.start(context, positionItem - 1);
                    } else {
                        WordStudyActivity.start(context, new ArrayList(cetRootWords), positionItem - 1);
                    }
                    break;
            }
            isChecked = !isChecked;
            animator.start();
            animator_1.start();
            setItemVisible();
        }

        private void setItemVisible() {
            if (isChecked) {
                def.setVisibility(View.VISIBLE);
                pron.setVisibility(View.GONE);
                word.setVisibility(View.GONE);
                info_img.setVisibility(View.VISIBLE);
            } else {
                def.setVisibility(View.GONE);
                pron.setVisibility(View.VISIBLE);
                word.setVisibility(View.VISIBLE);
                info_img.setVisibility(View.GONE);
            }
        }
    }

    public static class GroupItemViewHolder extends RecyclerView.ViewHolder {

        public GroupItemViewHolder(View itemView) {
            super(itemView);
        }

    }

    @Override
    public int getItemViewType(int position) {
        for (ViewHeader header : headers) {
            if (header.getIndex() == position)
                return -1;
        }
        return position;
    }
}
