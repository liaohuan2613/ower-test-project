package com.lhk;

import com.lhk.mongodb.MongoFindSameApplication;
import com.lhk.mongodb.MongoMergeTagsApplication;
import com.lhk.mongodb.MongoTemplateApplication;
import com.lhk.mysql.RebuildTagsApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TemplateApplication {

	public static void main(String[] args) {
		MongoFindSameApplication.main(null);
	}

}
