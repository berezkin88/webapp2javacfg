package com.javaTask.controller;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.javaTask.service.impl.UserServiceImpl;
import com.javaTask.model.User;
import com.javaTask.model.enums.Status;
import com.javaTask.model.Cart;
import com.javaTask.model.Product;
import com.javaTask.service.CartService;
import com.javaTask.service.ProductService;
import com.javaTask.service.impl.CartServiceImpl;
import com.javaTask.service.impl.ProductServiceImpl;
import com.javaTask.service.UserService;

/**
 * 
 * @author Aleksandr Beryozkin
 *
 *         This servlet is creating a new user and a new cart, pulling up all
 *         products and sending shop.jsp in response
 */

@WebServlet(name = "loginservlet", urlPatterns = "/loginservlet")
public class LoginServlet extends HttpServlet {

	private static final Logger LOG = Logger.getLogger(LoginServlet.class.getName());

	private static UserService us = new UserServiceImpl();
	private static ProductService ps = new ProductServiceImpl();
	private static CartService cs = new CartServiceImpl();
	private static List<Product> products;
	private static Cart cart = null;
	private static User user = null;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		createUserAndCart(req.getParameter("username"), req.getParameter("password"));

		products = ps.getAll();

		req.setAttribute("products", products);
		req.setAttribute("userId", user.getId());
		req.setAttribute("cartId", cart.getId());

		RequestDispatcher view = req.getRequestDispatcher("jsp/shop.jsp");
		view.forward(req, resp);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String userId = req.getParameter("userid");
		products = ps.getAll();

		if (userId.isEmpty()) {
			RequestDispatcher view = req.getRequestDispatcher("jsp/index.jsp");
			view.forward(req, resp);
		}
		
		cart = createNewCart(Integer.valueOf(userId));

		req.setAttribute("products", products);
		req.setAttribute("userId", userId);
		req.setAttribute("cartId", cart.getId());

		RequestDispatcher view = req.getRequestDispatcher("jsp/shop.jsp");
		view.forward(req, resp);
	}

	private void createUserAndCart(String username, String password) {
		LOG.info("Creating user... ");

		if (user == null)
			user = new User();

		user.setUsername(username);
		user.setPassword(password);

		us.insert(user);

		user = us.getOneByUsernamed(user.getUsername());
		LOG.info(user.toString());

		if (cart == null)
			cart = new Cart();

		LOG.info("initializing cart...");
		cart.setUserId(user.getId());
		cart.setStatus(Status.OPEN);
		cart.setTime(System.currentTimeMillis());

		cs.createCart(cart);

		cart = cs.getCartsByUserIdAndOpen(user.getId());

		LOG.info(cart.toString());
	}
	
	private Cart createNewCart(int userId) {
		Cart newCart = new Cart();
		
		LOG.info("initializing cart...");
		newCart.setUserId(user.getId());
		newCart.setStatus(Status.OPEN);
		newCart.setTime(System.currentTimeMillis());

		cs.createCart(newCart);

		newCart = cs.getCartsByUserIdAndOpen(userId);

		LOG.info("new cart created" + newCart.toString());
		
		return newCart;
	}

	public static Cart getCart() {
		return cart;
	}
}
