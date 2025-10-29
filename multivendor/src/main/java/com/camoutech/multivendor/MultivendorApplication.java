package com.camoutech.multivendor;

import com.camoutech.multivendor.chat.ToolsChat;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MultivendorApplication {

	public static void main(String[] args) {
		SpringApplication.run(MultivendorApplication.class, args);
	}


	@Bean
	public MethodToolCallbackProvider getMethodToolCallBackProvider(ToolsChat toolsChats) {
		return MethodToolCallbackProvider.builder()
				.toolObjects(toolsChats)
				.build();
	}

}
