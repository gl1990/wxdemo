package weChatServlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ota.util.VersionInfoItem;
import com.ota.util.XmlHelper;

/**
 * Servlet implementation class DownloadServlet
 */
@WebServlet("/DownloadServlet")
public class DownloadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DownloadServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//获取文件名,版本号
	    String product = request.getParameter("product");
	    String version = request.getParameter("version");
	    //防止读取name名乱码
	    product = new String(product.getBytes("iso-8859-1"), "utf-8");
	    version = new String(version.getBytes("iso-8859-1"), "utf-8");
	    //在控制台打印文件名
	    System.out.println("product：" + product);
	    System.out.println("version：" + version);
	    String currPath = request.getServletContext().getRealPath("./");
		String web_inf = currPath + "WEB-INF";
		String manifastPath = web_inf + File.separator + "manifast.xml";
		HashMap<String, VersionInfoItem> map = null;
		if (XmlHelper.checkManifast(manifastPath)) {
			map = XmlHelper.getVersionList(manifastPath, product);
		}
		Set<Entry<String, VersionInfoItem>> keyset = map.entrySet();
		for (Entry<String, VersionInfoItem> entry : keyset) {
			if (entry.getKey().equals(version)) {
				System.out.println(entry.getValue().getPackagePath());
				String filename = entry.getValue().getPackagePath();
				System.out.println(getServletContext().getMimeType(filename));
				//设置文件MIME类型  
				response.setContentType(getServletContext().getMimeType(filename));
				//获取要下载的文件绝对路径，我的文件都放到WebProject\WEB-INF\packages目录下
				ServletContext context = request.getServletContext();
				String fullFileName = context.getRealPath("./" + "WEB-INF/" +filename);
				filename = filename.substring(filename.lastIndexOf('/') + 1);
			    //设置Content-Disposition  
				response.setHeader("Content-Disposition", "attachment;filename=" + filename);
				System.out.println(fullFileName);
				//输入流为项目文件，输出流指向浏览器
				InputStream is = new FileInputStream(fullFileName);
				ServletOutputStream os =response.getOutputStream();
				/*
			     * 设置缓冲区
			     * is.read(b)当文件读完时返回-1
			     */
			    
			    int len=-1;
			    byte[] b=new byte[1024];
			    while((len=is.read(b))!=-1){
			        os.write(b,0,len);
			    }
			    //关闭流
			    is.close();
			    os.close();
				break;
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
