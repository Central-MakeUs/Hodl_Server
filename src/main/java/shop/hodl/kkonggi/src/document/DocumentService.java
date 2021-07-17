package shop.hodl.kkonggi.src.document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shop.hodl.kkonggi.utils.JwtService;

@Service
public class DocumentService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DocumentProvider documentProvider;
    private final DocumentDao documentDao;
    private final JwtService jwtService;

    @Autowired
    public DocumentService(DocumentProvider documentProvider, DocumentDao documentDao, JwtService jwtService) {
        this.documentProvider = documentProvider;
        this.documentDao = documentDao;
        this.jwtService = jwtService;
    }
}
