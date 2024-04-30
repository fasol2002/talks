package com.remi.web.spring;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.ai.ollama.api.OllamaApi.Message;
import org.springframework.ai.ollama.api.OllamaApi.Message.Role;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.remi.spring.OllamaService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.annotation.Resource;

@Controller
@RequestMapping(value = "/spring")
public class WebController {

	@Resource 
	private OllamaService ollamaService;
	
	public WebController() {
		initConversation();
	}

	private void initConversation() {
		conversationHistory.add(Message.builder(Role.ASSISTANT)
        .withContent("How can I help you ?")
        .build());
	}
	
	private static ArrayList<Message> conversationHistory = new ArrayList<>();
	
    @GetMapping(value = "/chat")
    public ModelAndView getChat() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("chat");
        mv.getModel().put("data", conversationHistory);
        return mv;
    }
    
    @PostMapping(value = "/chat")
    public ModelAndView postChat(@RequestBody HttpEntity<String> query) throws Exception {
    	conversationHistory.add(Message.builder(Role.USER)
    	        .withContent(query.getBody())
    	        .build());
    	conversationHistory.add(ollamaService.query(query.getBody()));
        return getChat(); 
    }
    
    @PostMapping(value = "/insert")
    public ModelAndView insert() throws IOException {
    	ollamaService.insert();
        return getChat();
    }
    
    @PostMapping(value = "/clearChroma")
    public ModelAndView clearChroma() throws IOException {
    	ollamaService.clearChroma();
        return getChat();
    }
    
    @PostMapping(value = "/clearChat")
    public ModelAndView clearChat() throws IOException {
    	conversationHistory.clear();
    	ollamaService.clearChatMemory();
    	initConversation();
        return getChat();
    }
	
}