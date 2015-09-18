package it.jaschke.alexandria;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.services.BookService;
import it.jaschke.alexandria.services.DownloadImage;


public class BookDetail extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String EAN_KEY = "EAN";
    private final int LOADER_ID = 10;
    private View rootView;
    private String ean;
    private String bookTitle;
    private ShareActionProvider shareActionProvider;
    private Button btnDelete;
    private TextView txtBookTitle;
    private TextView txtBookSubtitle;
    private TextView txtCategories;
    private TextView txtAuthors;
    private TextView txtBookDescription;

    public BookDetail(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            ean = arguments.getString(BookDetail.EAN_KEY);
            getLoaderManager().restartLoader(LOADER_ID, null, this);
        }

        rootView = inflater.inflate(R.layout.fragment_full_book, container, false);
        btnDelete = (Button) rootView.findViewById(R.id.delete_button);
        txtBookTitle = (TextView) rootView.findViewById(R.id.fullBookTitle);
        txtBookSubtitle = (TextView) rootView.findViewById(R.id.fullBookSubTitle);
        txtAuthors = (TextView) rootView.findViewById(R.id.authors);
        txtCategories = (TextView) rootView.findViewById(R.id.categories);
        txtBookDescription = (TextView) rootView.findViewById(R.id.fullBookDesc);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(BookService.EAN, ean);
                bookIntent.setAction(BookService.DELETE_BOOK);
                getActivity().startService(bookIntent);
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.book_detail, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        setupShareIntent();
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(ean)),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }

        bookTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
        txtBookTitle.setText(bookTitle);
        txtBookTitle.setContentDescription(getString(R.string.desc_book_title) + bookTitle);

        setupShareIntent();

        String bookSubTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
        txtBookSubtitle.setText(bookSubTitle);
        if(bookSubTitle != null && !bookSubTitle.isEmpty()) {
            txtBookSubtitle.setContentDescription(getString(R.string.desc_book_subtitle) + bookSubTitle);
        }

        String desc = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.DESC));
        txtBookDescription.setText(desc);
        if(desc != null && !desc.isEmpty()){
            txtBookDescription.setContentDescription(getString(R.string.desc_book_description) + desc);
        } else {
            txtBookDescription.setText(getString(R.string.no_description));
        }

        String authors = data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));
        if(authors != null){
            String[] authorsArr = authors.split(",");
            txtAuthors.setLines(authorsArr.length);
            txtAuthors.setText(authors.replace(",", "\n"));
            txtAuthors.setContentDescription(getString(R.string.desc_book_authors) + authors);
        } else {
            txtAuthors.setText(getString(R.string.no_authors_message));
        }
        String imgUrl = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
        if(Patterns.WEB_URL.matcher(imgUrl).matches()){
            new DownloadImage((ImageView) rootView.findViewById(R.id.fullBookCover)).execute(imgUrl);
            rootView.findViewById(R.id.fullBookCover).setVisibility(View.VISIBLE);
        }

        String categories = data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));
        txtCategories.setText(categories);
        if(categories != null && !categories.isEmpty()){
            txtCategories.setContentDescription(getString(R.string.desc_book_categories) + categories);
        } else {
            txtCategories.setContentDescription(getString(R.string.no_book_categories));
        }

        if(rootView.findViewById(R.id.right_container)!=null){
            rootView.findViewById(R.id.backButton).setVisibility(View.INVISIBLE);
        }

        txtBookTitle.requestFocus();

    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    @Override
    public void onPause() {
        super.onDestroyView();
        if(MainActivity.IS_TABLET && rootView.findViewById(R.id.right_container)==null){
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    private void setupShareIntent(){
        if(shareActionProvider != null){
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text)+bookTitle);
            shareActionProvider.setShareIntent(shareIntent);
        }
    }
}