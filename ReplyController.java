package com.example.demo.mapper;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("board")
public class ReplyController {
	
	@Autowired
	private ResourceLoader resourceLoader;
	
    @Autowired // interface형 클래스 객체 자동생성
    private BoardMapper boardMapper;
	
	@GetMapping("reply/{parent_num}")
	public String show_repl_form(@PathVariable int parent_num, Model model) {
		model.addAttribute("parent_num",parent_num);
		return "reply_form"; 		
	}
	
	@RequestMapping("reply/upload") /*텍스트파일업로드*/
	@ResponseBody //별도의 jsp없이 바로 브라우저에서 출력
	public boolean insertReply(
			// @RequestParam("files") MultipartFile[] mfiles,
			HttpServletRequest request, 
			@RequestParam("author") String author,
			@RequestParam(name="parent_num") int pnum) {
		ServletContext context = request.getServletContext();
		
		BoardVO vo = new BoardVO();
		vo.setAuthor(author);
		vo.setTitle(request.getParameter("title"));
		vo.setContents(request.getParameter("contents"));
		vo.setPnum(pnum);
		
		boardMapper.replyInsert(vo);
		
		return true; 
	} 

}
