package com.hybris.paths;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner; 

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class TagsTests {

	@Value("${API_KEY}")
	String API_KEY;

	@Autowired
	MaterialUtil ytUtil;
	
	@Test
	public void testYouTubeUtil() throws Exception {
		
		String json = ytUtil.get("https://www.youtube.com/watch?v=1xo-0gCVhTU&t=1191s",0);
		assertEquals( ytUtil.title, "Introduction to Microservices, Docker, and Kubernetes"  );
		assertEquals( ytUtil.thumb, "https://i.ytimg.com/vi/1xo-0gCVhTU/maxresdefault.jpg"  );
		assertEquals( ytUtil.description, "Learn the basics of Microservices, Docker, and Kubernetes. Code demo starts at 18:45. I mess up the terminal for the first few minutes, but I fix it by 21:50. Audio gets echoey a few times, but it goes away quickly. Sorry about that!\\n\\nDeployment YAML: https://pastebin.com/rZa9Dm1w\\n\\nDockerfile: https://pastebin.com/SZA26rbg\\n\\nHow to Containerize a Node App: https://nodejs.org/en/docs/guides/nodejs-docker-webapp/\\n\\nPackage-lock Blog Post: https://medium.com/@Quigley_Ja/everything-you-wanted-to-know-about-package-lock-json-b81911aa8ab8"  );
		assertEquals( ytUtil.publishedAt, "2017-11-08T04:50:19.000Z"  );

    }
	
}
