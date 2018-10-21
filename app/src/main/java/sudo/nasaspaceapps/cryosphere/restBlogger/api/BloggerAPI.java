package sudo.nasaspaceapps.cryosphere.restBlogger.api;


import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Part;
import retrofit2.http.Path;
import sudo.nasaspaceapps.cryosphere.restBlogger.model.Item;
import sudo.nasaspaceapps.cryosphere.restBlogger.model.PostList;

public class BloggerAPI {
    private static final String key = "AIzaSyDcpxMlBSqdudV2M6fySRpRHbp41Cj6IVA";
    private static final String url = "https://www.googleapis.com/blogger/v3/blogs/9103472313335469262/posts/";

    //Interface for running Retrofit

    //Har Method ek URL ko point Karta hain

    public interface PostService

        //Jo humara URL hain wo relative  hota hain, yeh BASE URL mein append ho jayega
    {   @GET("?key="+key)
        Call<PostList> getPostList();

        //Jo humari post ID hain wo dynamic hain toh uske liye curly braces lagayenge aur method mein yeh key paas kar denge
        @GET("{postID}/?key="+key)
        //Yaha par hume batana padega ki jo post ki ID hain wo humari post ki ID hain
        Call<Item> getPostById(@Path("postID") String id);
        //(@Path("postID) humne string id ke saamne lagayi hain toh ab wo uss value ko as PostID lega jab bhi hum daalenge
    }


    //Ab hum Singleton design pattern ka use karenge jisse ki Retrofit ka object ek hi baar use ho paaye

    public  static PostService postService = null;

    public  static PostService getService(){
        //yaha hum check karenge ki post service ka object null hain agar wo null hoga tabhi hum naya object banyenge toh wo ek hi baar banega
        if(postService == null){
            //ab yaha hum retrofit ka object create karenge
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            //Uske baad hum iss retrofit ke object se apna post Service implement karenge
            postService = retrofit.create(PostService.class);
        }

        return postService;
    }
}
