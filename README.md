# Allegro API Client
Project is core client to work with Allegro API. 
It includes authorization (based on OAuth2) and communication with offer management endpoints.
You can fork this repo and develop it with your needs.
To work with use Allegro API documentation (https://developer.allegro.pl/documentation).
## Features
* Authorization using either Authorization Code Flow or Device Flow. 
* Basic offer management Allegro API methods, like creating offers, modifying, searching products.
* Batch offer modifications with statuses of asynchronous requests.
* OfferBuilder and OfferModificationBuilder classes to easily build offer JSON.
## Technologies
* Java 17
* Spring Boot 2.7.3 (+ Spring Data, Spring Webflux)

## Code Examples
Creating offer draft with basic description (wrapped in service to use beans):

    @Service
    public class OfferService {
        private final AllegroAppService authService;
        private final OffersAllegroApiDao offersDao;
        private final ImagesAllegroApiDao imagesDao;
        
        @Autowired
        public OfferService(AllegroAppService authService, OffersAllegroApiDao offersDao, ImagesAllegroApiDao imagesDao) {
            this.authService = authService;
            this.offersDao = offersDao;
            this.imagesDao = imagesDao;
        }
        
        public void createSimpleOfferDraft() throws InvalidClientIdException {
            Token token = authService.getToken("your_client_id");
            String imageUrl = "https://cdn.pixabay.com/photo/2018/08/09/10/46/telephone-3594206_960_720.jpg";
            String uploadedImageUrl = imagesDao.uploadByUrl(imageUrl, token).getString("location");
            
            JSONObject offer = OfferBuilder.get()
                .name("your test offer")
                .category("353")
                .images(List.of(uploadedImageUrl))
                .descriptionTextItem("<h1>Your description item</h1>")
                .descriptionImageItem(uploadedImageUrl)
                .build();
            
            offersDao.createOfferDraft(offer, token);
        }
    }
