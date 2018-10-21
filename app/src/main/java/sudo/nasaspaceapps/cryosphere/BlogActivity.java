package sudo.nasaspaceapps.cryosphere;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sudo.nasaspaceapps.cryosphere.restBlogger.api.BloggerAPI;
import sudo.nasaspaceapps.cryosphere.restBlogger.model.PostList;

public class BlogActivity extends AppCompatActivity {
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);

        //RecyclerView ka object banaya
        recyclerView = findViewById(R.id.post_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getData();
    }

    //Yaha Retrofit ko call kiya
    public void getData()
    {
        final Call<PostList> postList = BloggerAPI.getService().getPostList();

        postList.enqueue(new Callback<PostList>() {
            //Yeh sab apne aap aaya
            @Override
            public void onResponse(Call<PostList> call, Response<PostList> response) {
                PostList list = response.body();
                Toast.makeText(BlogActivity.this, "Success", Toast.LENGTH_SHORT).show();
                //Yaha pe data mila toh recyclerview yaha bind kiya
                recyclerView.setAdapter(new PostlistAdapter(BlogActivity.this, list.getItems()));
            }

            @Override
            public void onFailure(Call<PostList> call, Throwable t) {

            }
        });
    }
}
