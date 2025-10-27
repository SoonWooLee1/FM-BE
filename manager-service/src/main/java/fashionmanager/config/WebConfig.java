package fashionmanager.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // yml에서 설정값 주입 — 기본값은 현재 실행 경로(user.dir)/uploadFiles/
    @Value("${file.upload-root:${user.dir}/uploadFiles}")
    private String uploadDir2;

    @Value("${file.upload-root}")
    private String uploadRoot;

    private final String uploadDir = new File(
            System.getProperty("user.dir"),
            "../../Fashion-Manager-FE/public/images")
            .getAbsolutePath();


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /files/** 요청 → 실제 파일 경로로 매핑
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:///" + uploadDir2 + "/");

        String location = "file:///" + (uploadRoot.endsWith("/") ? uploadRoot : uploadRoot + "/");
        registry.addResourceHandler("/files/**")
                .addResourceLocations(location);

        registry.addResourceHandler("/images/**").addResourceLocations("file:///" + uploadDir + "/");
    }
}
