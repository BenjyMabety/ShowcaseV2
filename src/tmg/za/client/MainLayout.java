package tmg.za.client;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import tmg.za.client.FileEditor.FileEditor;
import tmg.za.client.FileEditor.FileService;
import tmg.za.client.FileEditor.FileServiceAsync;
import tmg.za.client.FileEditor.FileUploader;
import tmg.za.client.Login.Login;
import tmg.za.client.Resources.Resources;
import tmg.za.client.Snake.Fruit;
import tmg.za.client.Snake.Snake;
import tmg.za.client.SpaceForce.Asteroid;
import tmg.za.client.SpaceForce.Bullet;
import tmg.za.client.SpaceForce.SpaceForce;
import tmg.za.shared.Direction;
import tmg.za.shared.MyFoo.MyStyle;
import tmg.za.shared.Physics;

public class MainLayout extends Composite {

	private static MainLayoutUiBinder uiBinder = GWT.create(MainLayoutUiBinder.class);
	private final FileServiceAsync fileService = GWT.create(FileService.class);

	Resources resources = GWT.create(Resources.class);
	Physics physics = new Physics();

	interface MainLayoutUiBinder extends UiBinder<Widget, MainLayout> {
	}

	public static final String BALL = "BALL";
	public static final String SPACE = "SPACE";
	public static final String SNAKE = "SNAKE";

	@UiField
	VerticalPanel mainPanel;
	@UiField
	HorizontalPanel mainCanvas;
	@UiField
	VerticalPanel controlPanel;
	@UiField
	VerticalPanel canvasPanel;
	@UiField
	TextArea taCanvas;
	@UiField
	Label headerLabel;
	@UiField
	TextBox tbDocument;
	@UiField
	VerticalPanel buttonPanel;
	@UiField
	MyStyle style;
	FileEditor fileEditor;
	Login login;
	GuessingGame gg;
	Ball ball;
	SpaceForce sf;
	Snake snake;
	Timer t;
	ArrayList<Asteroid> asteroids = new ArrayList<Asteroid>();
	Fruit fruit = new Fruit();

	FileUploader upload = new FileUploader();

	public MainLayout() {
		initWidget(uiBinder.createAndBindUi(this));
		headerLabel.getElement().setAttribute("style", "font-weight: bold;font-size:100px;text-align:center;");
		login = new Login();
		gg = new GuessingGame();
		fileEditor = new FileEditor();
		ball = new Ball();
		sf = new SpaceForce();
		snake = new Snake();

		mainPanel.add(login.getPbLogin());
		mainPanel.add(gg.getPbGuess());
		mainPanel.add(fileEditor.getPbFileEditor());
		mainPanel.add(ball.getPbBall());
		mainPanel.add(sf.getPbSpaceForce());
		mainPanel.add(snake.getPbSnake());

		login.getPbLogin().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (t != null) {
					t.cancel();
				}
				login.center();
				login.setGlassEnabled(true);
				taCanvas.setVisible(false);
				tbDocument.setVisible(false);
				buttonPanel.setVisible(false);
				tbDocument.setText("");
				tbDocument.setReadOnly(true);
				mainCanvas.remove(ball.getImage());
				mainCanvas.remove(sf.getImage());
				mainCanvas.remove(snake.getImage());
				mainCanvas.remove(fruit.getImage());
				clearAsteroids();
				// ball.setLive(false);
				controlPanel.setVisible(false);
				setEnabled(false, true);
			}
		});
		gg.getPbGuess().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (t != null) {
					t.cancel();
				}
				gg.center();
				gg.setGlassEnabled(true);
				taCanvas.setVisible(false);
				tbDocument.setVisible(false);
				buttonPanel.setVisible(false);
				tbDocument.setText("");
				tbDocument.setReadOnly(true);
				mainCanvas.remove(ball.getImage());
				mainCanvas.remove(sf.getImage());
				mainCanvas.remove(snake.getImage());
				mainCanvas.remove(fruit.getImage());
				clearAsteroids();
				// ball.setLive(false);
				controlPanel.setVisible(false);
				setEnabled(false, true);
			}
		});

		ball.getPbBall().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (t != null) {
					t.cancel();
				}
				canvasPanel.setVisible(false);
				buttonPanel.setVisible(false);
				setupControlPanel(BALL);
				controlPanel.setVisible(true);
				mainCanvas.add(ball.getImage().asWidget());
				// ball.setLive(true);
				mainCanvas.remove(sf.getImage());
				mainCanvas.remove(snake.getImage());
				mainCanvas.remove(fruit.getImage());
				clearAsteroids();
				setEnabled(true, true);
				mainCanvas.getParent().getElement().setAttribute("style",
						"position: absolute; inset: 0px;background-position:center;background-repeat:no-repeat");
			}
		});
		sf.getPbSpaceForce().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				canvasPanel.setVisible(false);
				buttonPanel.setVisible(false);
				controlPanel.setVisible(true);
				setupControlPanel(SPACE);
				mainCanvas.remove(ball.getImage());
				mainCanvas.remove(snake.getImage());
				mainCanvas.remove(fruit.getImage());
				// ball.setLive(false);
				setEnabled(true, false);
				mainCanvas.add(sf.getImage().asWidget());
				renderAsteroids();
				sf.setDistance(mainCanvas.getAbsoluteLeft());

				// left:735px;top:0px;
				// left:735px;top:195px;
				// left:735px;top:375px;

				if (t != null) {
					t.cancel();
				}

				t = new Timer() {

					@Override
					public void run() {
						redraw();
					}

					private void redraw() {

						mainCanvas.getParent().getElement().setAttribute("style",
								"position: absolute; inset: 0px;background-position:" + sf.getDistance()
										+ "px;background-repeat:repeat-x;");
						sf.setDistance(sf.getDistance() - physics.getSpace());
						for (int i = 0; i < asteroids.size(); i++) {
							asteroids.get(i).moveX(-physics.getForce(asteroids.get(i).getMass(), false, false));
							if (asteroids.get(i).getImage().getAbsoluteLeft() < 165) {
								asteroids.remove(asteroids.get(i));
								continue;
							}
							// asteroid.moveX(-1);
							if (physics.checkCollision(sf, asteroids.get(i))) {
								Window.alert("Game Over");
								sf.getImage().removeFromParent();
								resetSpace();
							}
						}
						if (asteroids.isEmpty()) {
							renderAsteroids();
						}

					}

				};
				t.scheduleRepeating(1000);

			}
		});
		snake.getPbSnake().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (t != null) {
					t.cancel();
				}
				snake.setDirection(Direction.RIGHT);
				t = new Timer() {

					@Override
					public void run() {
						redraw();
					}

					private void redraw() {
						if (snake.getDirection().equalsIgnoreCase(Direction.RIGHT)) {
							snake.moveX(30);
						} else if (snake.getDirection().equalsIgnoreCase(Direction.LEFT)) {
							snake.moveX(-30);
						} else if (snake.getDirection().equalsIgnoreCase(Direction.UP)) {
							snake.moveY(-30);
						} else {
							snake.moveY(30);

						}
						snake.move();
						if (snake.eat(fruit)) {
							spawnFruit(true);
							if (snake.getChildren().size() == 100) {
								Window.alert("Game Over");
								snake.reset();
							}
						}
					}
				};
				t.scheduleRepeating(1000);
				canvasPanel.setVisible(false);
				buttonPanel.setVisible(false);
				setupControlPanel(SNAKE);
				controlPanel.setVisible(true);
				mainCanvas.add(snake.getImage().asWidget());
				snake.setHead(true);
				// ball.setLive(true);
				mainCanvas.remove(sf.getImage());
				mainCanvas.remove(ball.getImage());
				mainCanvas.add(spawnFruit(false));
				clearAsteroids();
				setEnabled(false, true);
				mainCanvas.getParent().getElement().setAttribute("style",
						"position: absolute; inset: 0px;background-position:center;background-repeat:no-repeat");
			}

			/**
			 * @param eat
			 * @return
			 */
			private Widget spawnFruit(boolean eat) {
				// right 368->1118
				// top 0->530
				if (eat) {
					Snake child = new Snake();
					snake.addTail(child);
					mainCanvas.add(snake.getChildren().get(snake.getChildren().size() - 1).getImage());

				}
				double left = generateRandom(0, 780);
				double top = generateRandom(0, 530);
				fruit.getImage().getElement().getStyle().setLeft(left, Unit.PX);
				fruit.getImage().getElement().getStyle().setTop(top, Unit.PX);
				fruit.getImage().getElement().getStyle().setPosition(Position.ABSOLUTE);

				return fruit.getImage();
			}
		});
		setUpEditorButtonHandlers();

	}

	/**
	 * @param min
	 * @param max
	 * @return
	 */
	protected double generateRandom(int min, int max) {
		int value = (int) (Math.random() * (max - min + 1) + min);
		return value;
	}

	/**
	 * 
	 */
	protected void clearAsteroids() {
		for (Asteroid ast : asteroids) {
			ast.getImage().removeFromParent();
		}
		asteroids.clear();
	}

	/**
	 * 
	 */
	protected void renderAsteroids() {
		int gap = 0;
		int rightGap = 0;
		// left:735px;top:0px;
		// left:735px;top:195px;
		// left:735px;top:375px;
		for (int i = 0; i < 9; i++) {
			Asteroid asteroid = new Asteroid();
			asteroid.setRightStep(735 + rightGap);
			asteroid.setTopStep(i + gap);
			asteroid.getImage().getElement().getStyle().setPosition(Position.ABSOLUTE);
			gap = generateGap();
			mainCanvas.add(asteroid.getImage());
			asteroids.add(asteroid);
			if (i == 2) {
				gap = 0;
				rightGap = 140 * 3;
			}
			if (i == 5) {
				gap = 0;
				rightGap = 140 * 6;
			}

		}
	}

	/**
	 * @return
	 */
	private int generateGap() {
		int factor = (int) Math.round(Math.random() * 280);
		return factor;
	}

	/**
	 * 
	 */
	private void resetSpace() {
		clearAsteroids();
		mainCanvas.add(sf.getImage().asWidget());
		renderAsteroids();
		sf.setDistance(mainCanvas.getAbsoluteLeft());

	}

	/**
	 * 
	 */
	private void setupControlPanel(String mode) {
		controlPanel.clear();
		if (mode.equalsIgnoreCase(BALL)) {
			controlPanel.add(ball.getUpButton());
			HorizontalPanel hp = new HorizontalPanel();
			hp.add(ball.getLeftButton());
			hp.add(ball.getDownButton());
			hp.add(ball.getRightButton());
			controlPanel.add(hp);
			controlPanel.add(ball.getPbKeyboard());
			controlPanel.add(ball.getTbFriction());
			controlPanel.setCellHorizontalAlignment(hp, HasHorizontalAlignment.ALIGN_CENTER);
			controlPanel.setCellHorizontalAlignment(ball.getUpButton(), HasHorizontalAlignment.ALIGN_CENTER);
		} else if (mode.equalsIgnoreCase(SPACE)) {
			controlPanel.add(sf.getUpButton());
			HorizontalPanel hp = new HorizontalPanel();
			hp.add(sf.getLeftButton());
			hp.add(sf.getDownButton());
			hp.add(sf.getRightButton());
			if (!sf.getUpButton().isEnabled() && !sf.getDownButton().isEnabled()) {
				sf.getUpButton().setEnabled(true);
				sf.getDownButton().setEnabled(true);
			}
			sf.getLeftButton().setEnabled(false);
			sf.getRightButton().setEnabled(false);
			controlPanel.add(hp);
			controlPanel.add(sf.getPbKeyboard());
			sf.getPbKeyboard().addKeyUpHandler(new KeyUpHandler() {

				@Override
				public void onKeyUp(KeyUpEvent event) {
					/*
					 * if (event.getNativeKeyCode() == KeyCodes.KEY_RIGHT) {
					 * sf.moveX(physics.getForce(sf.getMass(), false, false)); } if
					 * (event.getNativeKeyCode() == KeyCodes.KEY_LEFT) {
					 * sf.moveX(-physics.getForce(sf.getMass(), false, false)); }
					 */
					if (event.getNativeKeyCode() == KeyCodes.KEY_UP) {
						sf.moveY(-physics.getForce(sf.getMass(), false, false));
					}
					if (event.getNativeKeyCode() == KeyCodes.KEY_DOWN) {
						sf.moveY(physics.getForce(sf.getMass(), false, false));
					}
					if (event.getNativeKeyCode() == KeyCodes.KEY_SPACE) {
						Bullet bullet = new Bullet();
						bullet.setTopStep(sf.getTopStep());
						mainCanvas.add(bullet.getImage().asWidget());
						Timer tBullet = new Timer() {

							@Override
							public void run() {
								if (sf.getImage().isAttached()) {
									bullet.moveX(physics.getForce(bullet.getMass(), false, false));
								}
								for (Asteroid asteroid : asteroids) {
									if (physics.checkCollision(bullet, asteroid)) {
										asteroid.setHits(1);
										if (asteroid.getHits() == asteroid.getMass()) {
											asteroid.getImage().removeFromParent();
											asteroids.remove(asteroid);
										}
										cancel();
										destroy();
									}
								}
								if (bullet.getImage().getAbsoluteLeft() > 1200) {
									cancel();
									destroy();
								}

							}

							private void destroy() {
								bullet.getImage().removeFromParent();
							}
						};
						tBullet.scheduleRepeating(50);
					}
				}
			});

			controlPanel.setCellHorizontalAlignment(hp, HasHorizontalAlignment.ALIGN_CENTER);
			controlPanel.setCellHorizontalAlignment(sf.getUpButton(), HasHorizontalAlignment.ALIGN_CENTER);

		} else {

			controlPanel.add(snake.getUpButton());
			HorizontalPanel hp = new HorizontalPanel();
			hp.add(snake.getLeftButton());
			hp.add(snake.getDownButton());
			hp.add(snake.getRightButton());
			controlPanel.add(hp);
			controlPanel.add(snake.getPbKeyboard());

			controlPanel.setCellHorizontalAlignment(hp, HasHorizontalAlignment.ALIGN_CENTER);
			controlPanel.setCellHorizontalAlignment(snake.getUpButton(), HasHorizontalAlignment.ALIGN_CENTER);

		}

	}

	/**
	 * 
	 */
	private void setUpEditorButtonHandlers() {

		buttonPanel.add(fileEditor.getNewButton());
		buttonPanel.add(fileEditor.getEditButton());
		buttonPanel.add(fileEditor.getSaveButton());
		buttonPanel.add(fileEditor.getLoadButton());

		ClickHandler openHandler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (t != null) {
					t.cancel();
				}
				taCanvas.setValue("");
				taCanvas.setVisible(true);
				tbDocument.setVisible(true);
				upload.getOpenButton().setEnabled(true);
				canvasPanel.setVisible(true);
				buttonPanel.setVisible(true);
				upload.center();
				upload.setGlassEnabled(true);
				tbDocument.setReadOnly(true);
				mainCanvas.remove(ball.getImage());
				mainCanvas.remove(sf.getImage());
				clearAsteroids();
				controlPanel.setVisible(false);
				setEnabled(false, true);
			}
		};
		fileEditor.getPbFileEditor().addClickHandler(openHandler);
		fileEditor.getLoadButton().addClickHandler(openHandler);
		upload.getCloseButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				taCanvas.setValue(upload.getData());
				tbDocument.setVisible(true);
				tbDocument.setValue(upload.getUpload().getFilename());
				tbDocument.setReadOnly(true);
				upload.hide();
			}
		});
		fileEditor.getNewButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				tbDocument.setReadOnly(false);
				if (upload.getUpload().getFilename().isBlank()) {
					tbDocument.setValue("Enter path here");
				}
				taCanvas.setValue("");

			}
		});
		fileEditor.getEditButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				taCanvas.setReadOnly(false);
				tbDocument.setReadOnly(true);
			}
		});
		fileEditor.getSaveButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				fileService.saveFile(tbDocument.getValue(), taCanvas.getValue(), new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getLocalizedMessage());
					}

					@Override
					public void onSuccess(String result) {
						taCanvas.setReadOnly(true);
						tbDocument.setReadOnly(true);
						Window.alert(result);
					}
				});

			}
		});

	}

	/**
	 * @param enabled
	 */
	void setEnabled(boolean enabled, boolean ball) {
		if (ball) {
			getElement().addClassName(enabled ? style.enabledBall() : style.disabled());
			getElement().addClassName(enabled ? style.enabledBall() : style.enabledSpace());
			getElement().removeClassName(enabled ? style.disabled() : style.enabledBall());
			getElement().removeClassName(enabled ? style.enabledSpace() : style.enabledBall());
		}

		else {
			getElement().addClassName(enabled ? style.enabledSpace() : style.disabled());
			getElement().addClassName(enabled ? style.enabledSpace() : style.enabledBall());
			getElement().removeClassName(enabled ? style.disabled() : style.enabledSpace());
			getElement().removeClassName(enabled ? style.enabledBall() : style.enabledSpace());
		}

	}

}
