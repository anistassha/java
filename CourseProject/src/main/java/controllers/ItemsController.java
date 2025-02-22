package main.java.controllers;

import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import main.ClientSocket;
import main.Entities.Product;
import main.Entities.ProductModel;
import main.Enums.RequestType;
import main.Enums.ResponseStatus;
import main.TCP.Request;
import main.TCP.Response;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import java.io.File;

public class ItemsController implements Initializable {
    private static final Gson GSON = new Gson();
    @FXML
    private Button addProducts_addBtn;

    @FXML
    private TableColumn<ProductModel, Number> addProducts_col_productId;

    @FXML
    private TableColumn<ProductModel, String> addProducts_col_productName;

    @FXML
    private TableColumn<ProductModel, String> addProducts_col_productType;

    @FXML
    private TableColumn<ProductModel, String> addProducts_col_productBrand;

    @FXML
    private TableColumn<ProductModel, Number> addProducts_col_productPrice;

    @FXML
    private TableColumn<ProductModel, Number> addProducts_col_productQuantity;

    @FXML
    private Button addProducts_deleteBtn;

    @FXML
    private ImageView addProducts_imageView;

    @FXML
    private Button addProducts_importBtn;

    @FXML
    private TextField addProducts_productBrand;

    @FXML
    private TextField addProducts_productId;

    @FXML
    private TextField addProducts_productName;

    @FXML
    private TextField addProducts_productPrice;

    @FXML
    private TextField addProducts_productQuantity;

    @FXML
    private ComboBox<String> addProducts_productType;

    @FXML
    private TextField addProducts_search;

    @FXML
    private TableView<ProductModel> addProducts_tableView;

    @FXML
    private Button addProducts_updateBtn;

    @FXML
    private Button addProducts_reset;

    private Image image;
    private String path;
    private String[] listCategory = {
            "Бытовая техника",
            "Компьютерная техника и периферия",
            "Мобильные устройства",
            "Техника для развлечений",
            "Осветительные приборы",
            "Электроинструменты",
            "Климатическая техника",
            "Электроника для автомобилей",
            "Энергетическое оборудование",
            "Системы умного дома",
            "Электроустановочные изделия"
    };

    public void addProductsCategory() {
        List<String> list = new ArrayList<>();

        for(String category : listCategory) {
            list.add(category);
        }

        ObservableList listData = FXCollections.observableArrayList(list);
        addProducts_productType.setItems(listData);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addProducts_col_productId.setCellValueFactory(new PropertyValueFactory<>("productId"));
        addProducts_col_productName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        addProducts_col_productType.setCellValueFactory(new PropertyValueFactory<>("productCategory"));
        addProducts_col_productBrand.setCellValueFactory(new PropertyValueFactory<>("manufacturer"));
        addProducts_col_productPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        addProducts_col_productQuantity.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));

        addProducts_search.textProperty().addListener((observable, oldValue, newValue) -> {
            filterProducts(newValue.trim());
        });

//        addProducts_col_productId.setCellValueFactory(cellData -> cellData.getValue().productId());
//        addProducts_col_productName.setCellValueFactory(cellData -> cellData.getValue().productNameProperty());
//        addProducts_col_productType.setCellValueFactory(cellData -> cellData.getValue().productCategoryProperty());
//        addProducts_col_productBrand.setCellValueFactory(cellData -> cellData.getValue().manufacturerProperty());
//        addProducts_col_productPrice.setCellValueFactory(cellData -> cellData.getValue().priceProperty());
//        addProducts_col_productQuantity.setCellValueFactory(cellData -> cellData.getValue().stockQuantityProperty());

        addProductsCategory();
        loadProducts();
    }

    private void loadProducts() {
        try {
            Request request = new Request();
            request.setRequestType(RequestType.GET_ALL_PRODUCTS);

            ClientSocket.getInstance().getOutStream().println(GSON.toJson(request));
            ClientSocket.getInstance().getOutStream().flush();

            String answer = ClientSocket.getInstance().getInStream().readLine();
            if (answer == null || answer.isEmpty()) {
                throw new RuntimeException("Ответ от сервера пустой");
            }

            Response response = GSON.fromJson(answer, Response.class);
            if (response == null) {
                throw new RuntimeException("Ответ от сервера не может быть преобразован в объект Response");
            }

            if (response.getResponseStatus() == ResponseStatus.OK) {
                String productsJson = response.getResponseData();
                ProductModel[] productsArray = GSON.fromJson(productsJson, ProductModel[].class);

                ObservableList<ProductModel> products = FXCollections.observableArrayList(productsArray);
                addProducts_tableView.setItems(products);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Ошибка загрузки данных!");
                alert.showAndWait();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addProductsImportImage() {
        FileChooser open = new FileChooser();
        open.setTitle("Open Image File");
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.jpeg");
        open.getExtensionFilters().add(imageFilter);

        File file = open.showOpenDialog(addProducts_tableView.getScene().getWindow());

        if (file != null) {
            path = file.getAbsolutePath();
            image = new Image(file.toURI().toString(), 134, 132, false, true);
            addProducts_imageView.setImage(image);
        }

    }

    public void addNewProduct() {
        try {
            if (addProducts_productName.getText().isEmpty() ||
                    addProducts_productType.getValue() == null ||
                    addProducts_productBrand.getText().isEmpty() ||
                    addProducts_productPrice.getText().isEmpty() ||
                    addProducts_productQuantity.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setContentText("Пожалуйста, заполните все поля перед добавлением продукта!");
                alert.showAndWait();
                return;
            }

            //int productId = Integer.parseInt(addProducts_productId.getText());

//            if (isProductExists(productId)) {
//                Alert alert = new Alert(Alert.AlertType.WARNING);
//                alert.setContentText("Продукт с ID " + productId + " уже существует!");
//                alert.showAndWait();
//                return;
//            }

            String productName = addProducts_productName.getText();
            String productCategory = addProducts_productType.getValue();
            String manufacturer = addProducts_productBrand.getText();
            double price = Double.parseDouble(addProducts_productPrice.getText());
            int stockQuantity = Integer.parseInt(addProducts_productQuantity.getText());
            String imageUrl = path != null ? path : "";

            ProductModel newProduct = new ProductModel();
            newProduct.setProductName(productName);
            newProduct.setProductCategory(productCategory);
            newProduct.setManufacturer(manufacturer);
            newProduct.setPrice(price);
            newProduct.setStockQuantity(stockQuantity);
            newProduct.setImage(imageUrl);


        //    ProductModel newProduct = new ProductModel(productId, productName, productCategory, manufacturer, price, stockQuantity, imageUrl);

            Request request = new Request();
            request.setRequestType(RequestType.ADD_PRODUCT);
            String productJson = GSON.toJson(newProduct);
            request.setRequestMessage(productJson);

            ClientSocket.getInstance().getOutStream().println(GSON.toJson(request));
            ClientSocket.getInstance().getOutStream().flush();

            String responseJson = ClientSocket.getInstance().getInStream().readLine();
            if (responseJson == null || responseJson.isEmpty()) {
                throw new RuntimeException("Ответ от сервера пустой");
            }
            Response response = GSON.fromJson(responseJson, Response.class);
            if (response == null) {
                throw new RuntimeException("Ответ от сервера не может быть преобразован в объект Response");
            }

            if (response.getResponseStatus() == ResponseStatus.OK) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Продукт успешно добавлен!");
                alert.showAndWait();
                loadProducts();
                clearAllFields();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Ошибка добавления продукта!");
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Произошла ошибка при добавлении продукта!");
            alert.showAndWait();
        }
    }

    public void addProductsSelect() {
        ProductModel selectedProduct = addProducts_tableView.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            return;
        }

        addProducts_productId.setText(String.valueOf(selectedProduct.getProductId()));
        addProducts_productName.setText(selectedProduct.getProductName());
        addProducts_productType.setValue(selectedProduct.getProductCategory());
        addProducts_productBrand.setText(selectedProduct.getManufacturer());
        addProducts_productPrice.setText(String.valueOf(selectedProduct.getPrice()));
        addProducts_productQuantity.setText(String.valueOf(selectedProduct.getStockQuantity()));
        if (selectedProduct.getImage() != null && !selectedProduct.getImage().isEmpty()) {
            File imageFile = new File(selectedProduct.getImage());
            if (imageFile.exists()) {
                Image img = new Image(imageFile.toURI().toString(), 134, 132, false, true);
                addProducts_imageView.setImage(img);
            } else {
                addProducts_imageView.setImage(null);
            }
        } else {
            addProducts_imageView.setImage(null);
        }
    }

    public void clearAllFields() {
        addProducts_productId.clear();
        addProducts_productName.clear();
        addProducts_productBrand.clear();
        addProducts_productPrice.clear();
        addProducts_productQuantity.clear();
        addProducts_productType.setValue(null);
        addProducts_imageView.setImage(null);
        path = null;
    }

    public void updateProduct() {
        try {
            if (addProducts_tableView.getSelectionModel().getSelectedItem() == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setContentText("Выберите продукт, который хотите обновить!");
                alert.showAndWait();
                return;
            }

            if (addProducts_productId.getText().isEmpty() ||
                    addProducts_productName.getText().isEmpty() ||
                    addProducts_productType.getValue() == null ||
                    addProducts_productBrand.getText().isEmpty() ||
                    addProducts_productPrice.getText().isEmpty() ||
                    addProducts_productQuantity.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setContentText("Пожалуйста, заполните все поля перед обновлением продукта!");
                alert.showAndWait();
                return;
            }

            int productId = Integer.parseInt(addProducts_productId.getText());
            String productName = addProducts_productName.getText();
            String productCategory = addProducts_productType.getValue();
            String manufacturer = addProducts_productBrand.getText();
            double price = Double.parseDouble(addProducts_productPrice.getText());
            int stockQuantity = Integer.parseInt(addProducts_productQuantity.getText());
            String imageUrl = path != null ? path : addProducts_tableView.getSelectionModel().getSelectedItem().getImage();

            ProductModel updatedProduct = new ProductModel(productId, productName, productCategory, manufacturer, price, stockQuantity, imageUrl);

            Request request = new Request();
            request.setRequestType(RequestType.UPDATE_PRODUCT);
            String productJson = GSON.toJson(updatedProduct);
            request.setRequestMessage(productJson);

            ClientSocket.getInstance().getOutStream().println(GSON.toJson(request));
            ClientSocket.getInstance().getOutStream().flush();

            String responseJson = ClientSocket.getInstance().getInStream().readLine();
            if (responseJson == null || responseJson.isEmpty()) {
                throw new RuntimeException("Ответ от сервера пустой");
            }
            Response response = GSON.fromJson(responseJson, Response.class);
            if (response == null) {
                throw new RuntimeException("Ответ от сервера не может быть преобразован в объект Response");
            }

            if (response.getResponseStatus() == ResponseStatus.OK) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Продукт успешно обновлен!");
                alert.showAndWait();
                loadProducts();
                clearAllFields();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Ошибка обновления продукта!");
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Произошла ошибка при обновлении продукта!");
            alert.showAndWait();
        }
    }

    public void deleteProduct() {
        try {
            ProductModel selectedProduct = addProducts_tableView.getSelectionModel().getSelectedItem();

            if (selectedProduct == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setContentText("Пожалуйста, выберите продукт для удаления!");
                alert.showAndWait();
                return;
            }

            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Подтверждение удаления");
            confirmAlert.setHeaderText(null);
            confirmAlert.setContentText("Вы уверены, что хотите удалить продукт \"" + selectedProduct.getProductName() + "\"?");
            if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
                return;
            }

            Request request = new Request();
            request.setRequestType(RequestType.DELETE_PRODUCT);
            String productJson = GSON.toJson(selectedProduct);
            request.setRequestMessage(productJson);

            ClientSocket.getInstance().getOutStream().println(GSON.toJson(request));
            ClientSocket.getInstance().getOutStream().flush();

            String responseJson = ClientSocket.getInstance().getInStream().readLine();
            if (responseJson == null || responseJson.isEmpty()) {
                throw new RuntimeException("Ответ от сервера пустой");
            }

            Response response = GSON.fromJson(responseJson, Response.class);
            if (response.getResponseStatus() == ResponseStatus.OK) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Продукт успешно удален!");
                alert.showAndWait();
                loadProducts();
                clearAllFields();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Ошибка удаления продукта!");
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Произошла ошибка при удалении продукта!");
            alert.showAndWait();
        }
    }

    private boolean isProductExists(int productId) throws IOException {
        Request checkRequest = new Request();
        checkRequest.setRequestType(RequestType.CHECK_PRODUCT_EXISTS);
        checkRequest.setRequestMessage(String.valueOf(productId));

        ClientSocket.getInstance().getOutStream().println(GSON.toJson(checkRequest));
        ClientSocket.getInstance().getOutStream().flush();

        String responseJson = ClientSocket.getInstance().getInStream().readLine();
        if (responseJson == null || responseJson.isEmpty()) {
            throw new RuntimeException("Ответ от сервера пустой");
        }

        Response response = GSON.fromJson(responseJson, Response.class);
        if (response == null) {
            throw new RuntimeException("Ответ от сервера не может быть преобразован в объект Response");
        }

        return response.getResponseStatus() == ResponseStatus.OK;
    }

    private void filterProducts(String query) {
        try {
            Request request = new Request();
            request.setRequestType(RequestType.SEARCH_PRODUCTS);
            request.setRequestMessage(query);

            ClientSocket.getInstance().getOutStream().println(GSON.toJson(request));
            ClientSocket.getInstance().getOutStream().flush();

            String responseJson = ClientSocket.getInstance().getInStream().readLine();
            if (responseJson == null || responseJson.isEmpty()) {
                throw new RuntimeException("Ответ от сервера пустой");
            }

            Response response = GSON.fromJson(responseJson, Response.class);
            if (response.getResponseStatus() == ResponseStatus.OK) {
                String productsJson = response.getResponseData();
                ProductModel[] productsArray = GSON.fromJson(productsJson, ProductModel[].class);

                ObservableList<ProductModel> products = FXCollections.observableArrayList(productsArray);
                addProducts_tableView.setItems(products);
            } else {
                addProducts_tableView.setItems(FXCollections.observableArrayList());
            }
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Ошибка при поиске продуктов!");
            alert.showAndWait();
        }
    }
}
