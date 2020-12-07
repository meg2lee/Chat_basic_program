package com.example.demo.mapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Controller
@RequestMapping("mybatis") // default url주소
public class BoardMybatisController {

	@Autowired
	private ResourceLoader resourceLoader;
	
    @Autowired // interface형 클래스 객체 자동생성
    private BoardMapper boardMapper;
        
    @GetMapping("board/list/page") /*메인(전체리스트화면)*/
    public String board_list(Model model, @RequestParam(value="num", defaultValue="1")int num) {   	    	
    	PageHelper.startPage(num, 7);
    	PageInfo<BoardVO> pageInfo = new PageInfo<>(boardMapper.getBoardList());  	
		model.addAttribute("pageInfo",pageInfo);
		model.addAttribute("boardlist",boardMapper.getBoardList());
    	return "my_board_list";
    }
    
    @GetMapping("board/list/page/{num}")
    public String getUserListPage(@PathVariable int num, Model model) {
    	PageHelper.startPage(num, 7);
    	PageInfo<BoardVO> pageInfo = new PageInfo<>(boardMapper.getBoardList());  	
		model.addAttribute("pageInfo",pageInfo);
		return "my_board_list";
    	
    }
    
    @RequestMapping("board/index")
    public String board_index(Model model, @RequestParam("num") int num) {
		model.addAttribute("indexInfo",boardMapper.getIndexList());
    	return "index";
    	
    }
        
    @GetMapping("board/list/page/{num}/upload") /*upload form페이지*/
    public String getForm() {
    	return "board_form";
    }
    
    @GetMapping("board/detail") /*상세페이지보기*/
    public String select_board(Model model, @RequestParam("num") int num) {
    	model.addAttribute("board",boardMapper.select(num));
    	model.addAttribute("view", boardMapper.countUpdate(num));
    	model.addAttribute("fileList", boardMapper.fileList(num));
		return "detail";   
    }
    
    @ResponseBody
    @RequestMapping("board/detail") /*수정하기*/
    public String board_update(@ModelAttribute BoardVO b) {
    	return boardMapper.updateBoard(b)+"";
    }
     
    @DeleteMapping("board/detail") /*삭제하기*/
    @ResponseBody
    public String board_delete(@RequestParam("num")int num) {
    	return boardMapper.deleteBoard(num)+"";
    }

    @PostMapping("board/upload") /*텍스트+멀티파일업로드*/
	@ResponseBody //별도의 jsp없이 바로 브라우저에서 출력
	public boolean insertBoard(@RequestParam("files")MultipartFile[] mfiles,
			HttpServletRequest request, 
			@RequestParam("author") String author
			) {
		ServletContext context = request.getServletContext();
		String savePath = context.getRealPath("/WEB-INF/upload");
		BoardVO vo = new BoardVO();
		vo.setAuthor(request.getParameter("author"));
		vo.setWdate(request.getParameter("wdate"));
		vo.setTitle(request.getParameter("title"));
		vo.setContents(request.getParameter("contents"));		
		
		boardMapper.add(vo);
		int board_key = boardMapper.addAndGetKey();
		System.out.println("board_key : "+board_key);
		try {
			for(int i=0;i<mfiles.length;i++) {
				if(!mfiles[i].isEmpty()) {
					mfiles[i].transferTo(
						new File(savePath+"/"+mfiles[i].getOriginalFilename()));
					AttachVO attach = new AttachVO();
					attach.setNum(board_key);
					attach.setFilename(mfiles[i].getOriginalFilename());
					attach.setFilesize(mfiles[i].getSize());
					attach.setContentType(mfiles[i].getContentType());
					boardMapper.attach_insert(attach);
				}
			}			
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@GetMapping("download/{filename}") /*멀티파일 다운로드*/
	public ResponseEntity<Resource> download( 
			/*파일은 query string으로 넘길 수 없으며, 위의 자료형으로 받아야함*/
			HttpServletRequest request,
			@PathVariable String filename){
		Resource resource = resourceLoader.getResource("WEB-INF/upload/"+filename);
		System.out.println("파일명:"+resource.getFilename());
		String contentType = null;
		try { /*폴더경로를 찾아, 그 곳에 출력*/
			contentType = request.getServletContext()
					.getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(contentType == null) { /*다운로드 창 띄워줌*/
			contentType = "application/octet-stream";
		}
		
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION,
						"attachment; filename=\""+resource.getFilename()+"\"")
				.body(resource);
		}	
	
	@PostMapping("/board/search")
	public String search(@RequestParam("searchType") String sas,
			@RequestParam("searchText") String sen, Model model,
			@RequestParam(name="num",defaultValue = "1") int num) {
		PageHelper.startPage(num, 5);
		System.out.println(sas);
		System.out.println(sen);
    	PageInfo<BoardVO> pageInfo = new PageInfo<>(boardMapper.search(sas, sen));  	
		model.addAttribute("pageInfo",pageInfo);
		return "my_board_list";
		
	}
   
}
