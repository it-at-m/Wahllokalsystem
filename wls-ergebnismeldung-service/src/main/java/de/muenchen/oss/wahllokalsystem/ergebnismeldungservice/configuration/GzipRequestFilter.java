package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import lombok.val;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class GzipRequestFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if ("gzip".equalsIgnoreCase(request.getHeader("Content-Encoding"))) {
            val compressed = request.getInputStream().readAllBytes();

            try (GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(compressed))) {
                val uncompressed = gis.readAllBytes();
                HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(request) {
                    @Override
                    public ServletInputStream getInputStream() {
                        ByteArrayInputStream bais = new ByteArrayInputStream(uncompressed);
                        return new ServletInputStream() {
                            @Override
                            public int read() {
                                return bais.read();
                            }

                            @Override
                            public boolean isFinished() {
                                return bais.available() == 0;
                            }

                            @Override
                            public boolean isReady() {
                                return true;
                            }

                            @Override
                            public void setReadListener(ReadListener listener) {}
                        };
                    }
                };

                filterChain.doFilter(wrapper, response);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
