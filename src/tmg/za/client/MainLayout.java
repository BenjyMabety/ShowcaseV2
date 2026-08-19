package tmg.za.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import com.google.gwt.user.client.ui.Image;
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
import tmg.za.client.Pacman.Ghost;
import tmg.za.client.Pacman.Pacman;
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
	public static final String PACMAN = "PACMAN";

	// Load MAP_1 dynamically from Map_1.txt in Resources
	public String[] MAP_1 = resources.map1().getText().split("\\r?\\n");

	private Image[][] fruitGrid;
	private Image[][] riceGrid;
	private final List<Widget> mapWidgets = new ArrayList<>();
	private int riceChompCounter = 0;
	private int totalRiceRemaining = 0;
	private boolean isGameFrozen = false;
	private int timerInterval = 600; // Base Pacman move timer in ms

	private final List<Ghost> activeGhosts = new ArrayList<>();
	private Timer ghostDespawnTimer;
	private boolean isWaitingForInput = true;

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
	Pacman pacMan;
	// Track the active image widget on the canvas
	private Image currentPacmanImage = null;
	Timer t;
	private int currentSpeed = 1000; // Starts at 1 step per 1000ms
	private int score = 0;
	private com.google.gwt.user.client.ui.Label scoreLabel = new com.google.gwt.user.client.ui.Label("Score: 0");

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
		pacMan = new Pacman();

		mainPanel.add(login.getPbLogin());
		mainPanel.add(gg.getPbGuess());
		mainPanel.add(fileEditor.getPbFileEditor());
		mainPanel.add(ball.getPbBall());
		mainPanel.add(sf.getPbSpaceForce());
		mainPanel.add(snake.getPbSnake());
		mainPanel.add(pacMan.getPbPacman());

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
				mainCanvas.remove(pacMan.getImage());
				clearPacmanAssets();
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
				mainCanvas.remove(pacMan.getImage());
				clearPacmanAssets();
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
				mainCanvas.remove(pacMan.getImage());
				clearPacmanAssets();
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
				mainCanvas.remove(pacMan.getImage());
				// ball.setLive(false);
				setEnabled(true, false);
				mainCanvas.add(sf.getImage().asWidget());
				renderAsteroids();
				clearPacmanAssets();
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
				// Add this inside your onClick handler right where the game starts up:
				score = 0;
				scoreLabel.setText("Score: 0");
				currentSpeed = 1000;

				snake.setDirection(Direction.RIGHT);
				t = new Timer() {

					@Override
					public void run() {
						redraw();
					}

					private void redraw() {
						// 1. Safely extract current head location strings
						String headLeftStr = snake.getImage().getElement().getStyle().getLeft().replace("px", "")
								.trim();
						String headTopStr = snake.getImage().getElement().getStyle().getTop().replace("px", "").trim();

						// 2. Fallback to 0 if styles are blank at game start to avoid
						// NumberFormatException
						double headLeft = headLeftStr.isEmpty() ? 0.0 : Double.valueOf(headLeftStr);
						double headTop = headTopStr.isEmpty() ? 0.0 : Double.valueOf(headTopStr);

						// 3. Save coordinates as history for the tail children
						snake.setLastLeftVal(headLeft);
						snake.setLastTopVal(headTop);

						// 4. Drive head forward into its new layout space
						if (snake.getDirection().equalsIgnoreCase(Direction.RIGHT)) {
							snake.moveX(30);
						} else if (snake.getDirection().equalsIgnoreCase(Direction.LEFT)) {
							snake.moveX(-30);
						} else if (snake.getDirection().equalsIgnoreCase(Direction.UP)) {
							snake.moveY(-30);
						} else {
							snake.moveY(30);
						}

						// 5. Command children to cascade over step history checkpoints
						snake.move();

						// 6. Check Self-Collision (Head hitting any tail segment)
						// Extract head's post-movement coordinates to compare with children
						String currentHeadLeft = snake.getImage().getElement().getStyle().getLeft().replace("px", "")
								.trim();
						String currentHeadTop = snake.getImage().getElement().getStyle().getTop().replace("px", "")
								.trim();
						double hLeft = currentHeadLeft.isEmpty() ? 0.0 : Double.valueOf(currentHeadLeft);
						double hTop = currentHeadTop.isEmpty() ? 0.0 : Double.valueOf(currentHeadTop);

						for (Snake child : snake.getChildren()) {
							String childLeftStr = child.getImage().getElement().getStyle().getLeft().replace("px", "")
									.trim();
							String childTopStr = child.getImage().getElement().getStyle().getTop().replace("px", "")
									.trim();
							double cLeft = childLeftStr.isEmpty() ? 0.0 : Double.valueOf(childLeftStr);
							double cTop = childTopStr.isEmpty() ? 0.0 : Double.valueOf(childTopStr);

							// If head coordinates perfectly match a tail segment coordinate, end the game
							// If head coordinates perfectly match a tail segment coordinate, end the game
							if (hLeft == cLeft && hTop == cTop) {
								t.cancel(); // Stop the game loop timer immediately
								Window.alert("Game Over! You bit your tail.");

								// 1. Clear out tail children and reset head position
								snake.reset();

								// 2. Clear out score tracking variables back to zero
								score = 0;
								scoreLabel.setText("Score: 0");

								// 3. Re-initialize baseline starting speed delay
								currentSpeed = 1000;

								// 4. Automatically re-launch the game timer loop instantly
								t.scheduleRepeating(currentSpeed);
								return; // Exit the loop and redraw execution early
							}

						}

						// 7. Evaluate map food layout flags (Removed the 100 child cap restriction)
						// Evaluate map food layout flags
						// Evaluate map food layout flags
						if (snake.eat(fruit)) {
							spawnFruit(true);

							// Increment score and refresh the display label text
							score += 10;
							scoreLabel.setText("Score: " + score);

							// Double the movement speed by cutting the loop interval time in half
							if (currentSpeed > 62) {
								currentSpeed = currentSpeed / 2;
								t.cancel();
								t.scheduleRepeating(currentSpeed);
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
				mainCanvas.remove(pacMan.getImage());
				mainCanvas.add(spawnFruit(false));
				clearAsteroids();
				clearPacmanAssets();
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

		pacMan.getPbPacman().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (t != null) {
					t.cancel();
				}
				despawnGhosts();

				canvasPanel.setVisible(false);
				buttonPanel.setVisible(false);

				// Clear assets from other game modes
				mainCanvas.remove(sf.getImage());
				mainCanvas.remove(snake.getImage());
				mainCanvas.remove(fruit.getImage());
				mainCanvas.remove(ball.getImage());
				clearAsteroids();

				// Perform full game reset (Score = 0, reloads Map_1.txt, positions Pacman)
				resetPacmanGame(true);

				setupControlPanel(PACMAN);
				controlPanel.setVisible(true);
				setEnabled(false, true);
				mainCanvas.getParent().getElement().setAttribute("style",
						"position: absolute; inset: 0px;background-position:center;background-repeat:no-repeat");
			}
		});
		setUpEditorButtonHandlers();

	}

	private void redraw() {
		if (isGameFrozen)
			return;

		Image activeFace = pacMan.getImage();
		if (currentPacmanImage != activeFace) {
			mainCanvas.remove(currentPacmanImage);
			currentPacmanImage = activeFace;
			mainCanvas.add(currentPacmanImage);
		}

		String dir = pacMan.getDirection();

		if (canMove(pacMan.getRightStep(), pacMan.getTopStep(), dir)) {
			if (Direction.RIGHT.equalsIgnoreCase(dir)) {
				pacMan.moveX(30);
			} else if (Direction.LEFT.equalsIgnoreCase(dir)) {
				pacMan.moveX(-30);
			} else if (Direction.UP.equalsIgnoreCase(dir)) {
				pacMan.moveY(-30);
			} else if (Direction.DOWN.equalsIgnoreCase(dir)) {
				pacMan.moveY(30);
			}

			checkItemCollision();
			checkGhostCollision();
		}
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

	public void clearPacmanAssets() {

		// 2. Clear all active ghosts and cancel despawn timer
		despawnGhosts();

		// 3. Remove active Pacman sprite
		if (currentPacmanImage != null) {
			mainCanvas.remove(currentPacmanImage);
			currentPacmanImage = null;
		}

		// 4. Remove all brick, rice, and fruit map widgets
		if (mapWidgets != null) {
			for (Widget w : mapWidgets) {
				mainCanvas.remove(w);
			}
			mapWidgets.clear();
		}

		// 5. Clear grid array references
		riceGrid = null;
		fruitGrid = null;

		// 6. Reset state variables
		isGameFrozen = false;
		riceChompCounter = 0;
		totalRiceRemaining = 0;
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
	 * Resets Pacman to grid[1, 1] facing RIGHT awaiting input.
	 * 
	 * @param fullReset If true, resets score to 0 and reloads original Map_1.txt.
	 *                  If false, preserves current score and uses existing MAP_1
	 *                  array.
	 */
	private void resetPacmanGame(boolean fullReset) {
		if (t != null) {
			t.cancel();
			t = null;
		}

		despawnGhosts();
		isGameFrozen = false;
		isWaitingForInput = true;
		riceChompCounter = 0;
		timerInterval = 600;

		if (fullReset) {
			score = 0;
			scoreLabel.setText("Score: " + score);
			// Reload original stage 1 layout from file resource
			MAP_1 = resources.map1().getText().split("\\r?\\n");
		}

		// Re-render map tiles (uses active MAP_1 layout)
		renderMap();

		// Position Pacman at grid[1, 1] facing RIGHT
		pacMan.setRightStep(30);
		pacMan.setTopStep(30);
		pacMan.setDirection(Direction.RIGHT);

		// Refresh canvas image reference
		if (currentPacmanImage != null) {
			mainCanvas.remove(currentPacmanImage);
		}
		currentPacmanImage = pacMan.getImage();
		mainCanvas.add(currentPacmanImage);
		pacMan.updatePosition();
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

		} else if (mode.equalsIgnoreCase(SNAKE)) {

			controlPanel.add(snake.getUpButton());
			HorizontalPanel hp = new HorizontalPanel();
			hp.add(snake.getLeftButton());
			hp.add(snake.getDownButton());
			hp.add(snake.getRightButton());
			controlPanel.add(hp);
			controlPanel.add(snake.getPbKeyboard());

			controlPanel.setCellHorizontalAlignment(hp, HasHorizontalAlignment.ALIGN_CENTER);
			controlPanel.setCellHorizontalAlignment(snake.getUpButton(), HasHorizontalAlignment.ALIGN_CENTER);
			// Add this where you set up your control layout configurations
			scoreLabel.getElement().getStyle().setProperty("textAlign", "center");
			scoreLabel.getElement().getStyle().setProperty("fontSize", "20px");
			scoreLabel.getElement().getStyle().setProperty("fontWeight", "bold");
			scoreLabel.getElement().getStyle().setProperty("marginTop", "15px");
			scoreLabel.getElement().getStyle().setProperty("fontFamily", "sans-serif");
			scoreLabel.getElement().getStyle().setProperty("color", "#333");

			// Add the score label right below your arrow button layout panel
			controlPanel.add(scoreLabel);

		} else {
			int force = physics.getForce(pacMan.getMass(), false, false);

			// UP Button
			pacMan.getUpButton().addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					changePacmanDirection(Direction.UP);
				}
			});
			controlPanel.add(pacMan.getUpButton());

			HorizontalPanel hp = new HorizontalPanel();

			// LEFT Button
			pacMan.getLeftButton().addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					changePacmanDirection(Direction.LEFT);
				}
			});
			hp.add(pacMan.getLeftButton());

			// DOWN Button
			pacMan.getDownButton().addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					changePacmanDirection(Direction.DOWN);
				}
			});
			hp.add(pacMan.getDownButton());

			// RIGHT Button
			pacMan.getRightButton().addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					changePacmanDirection(Direction.RIGHT);
				}
			});
			hp.add(pacMan.getRightButton());

			controlPanel.add(hp);
			controlPanel.add(pacMan.getPbKeyboard());

			// Keyboard Handler
			pacMan.getPbKeyboard().addKeyUpHandler(new KeyUpHandler() {
				@Override
				public void onKeyUp(KeyUpEvent event) {
					int keyCode = event.getNativeKeyCode();

					if (keyCode == KeyCodes.KEY_RIGHT) {
						changePacmanDirection(Direction.RIGHT);
					} else if (keyCode == KeyCodes.KEY_LEFT) {
						changePacmanDirection(Direction.LEFT);
					} else if (keyCode == KeyCodes.KEY_UP) {
						changePacmanDirection(Direction.UP);
					} else if (keyCode == KeyCodes.KEY_DOWN) {
						changePacmanDirection(Direction.DOWN);
					}
				}
			});

			controlPanel.setCellHorizontalAlignment(hp, HasHorizontalAlignment.ALIGN_CENTER);
			controlPanel.setCellHorizontalAlignment(pacMan.getUpButton(), HasHorizontalAlignment.ALIGN_CENTER);

			// Score Label Configuration for Pacman Mode
			scoreLabel.getElement().getStyle().setProperty("textAlign", "center");
			scoreLabel.getElement().getStyle().setProperty("fontSize", "20px");
			scoreLabel.getElement().getStyle().setProperty("fontWeight", "bold");
			scoreLabel.getElement().getStyle().setProperty("marginTop", "15px");
			scoreLabel.getElement().getStyle().setProperty("fontFamily", "sans-serif");
			scoreLabel.getElement().getStyle().setProperty("color", "#333");

			controlPanel.add(scoreLabel);
		}

	}

	private void changePacmanDirection(String newDirection) {
		if (isGameFrozen)
			return;

		// Start movement loop on first input key/button press after game reset
		if (isWaitingForInput) {
			isWaitingForInput = false;
			startPacmanTimer();
		}

		// Ignore if already moving in this direction
		if (newDirection.equalsIgnoreCase(pacMan.getDirection())) {
			return;
		}

		// Ignore turn if target path is blocked by brick
		if (!canMove(pacMan.getRightStep(), pacMan.getTopStep(), newDirection)) {
			return;
		}

		Image oldImage = pacMan.getImage();
		pacMan.setDirection(newDirection);
		Image newImage = pacMan.getImage();

		if (oldImage != newImage && currentPacmanImage != newImage) {
			mainCanvas.remove(currentPacmanImage);
			currentPacmanImage = newImage;
			mainCanvas.add(currentPacmanImage);
			pacMan.updatePosition();
		}
	}

	private void startPacmanTimer() {
		if (t != null) {
			t.cancel();
		}
		t = new Timer() {
			@Override
			public void run() {
				redraw();
			}
		};
		t.scheduleRepeating(timerInterval);
	}

	public void renderMap() {
		for (Widget w : mapWidgets) {
			mainCanvas.remove(w);
		}
		mapWidgets.clear();

		int rows = MAP_1.length;
		int cols = MAP_1[0].length();
		riceGrid = new Image[rows][cols];
		fruitGrid = new Image[rows][cols];
		totalRiceRemaining = 0;

		int tileSize = 30;
		int brickSize = 28;
		int brickOffset = (tileSize - brickSize) / 2;
		int riceSize = 8;
		int riceOffset = (tileSize - riceSize) / 2;
		int fruitSize = 20;
		int fruitOffset = (tileSize - fruitSize) / 2;

		for (int r = 0; r < rows; r++) {
			String line = MAP_1[r];
			for (int c = 0; c < line.length(); c++) {
				char cell = line.charAt(c);

				if (cell == '-') {
					Image brick = new Image(resources.brick());
					brick.setPixelSize(brickSize, brickSize);
					brick.getElement().getStyle().setPosition(Position.ABSOLUTE);
					brick.getElement().getStyle().setLeft((c * tileSize) + brickOffset, Unit.PX);
					brick.getElement().getStyle().setTop((r * tileSize) + brickOffset, Unit.PX);

					mainCanvas.add(brick);
					mapWidgets.add(brick);
				} else if (cell == '.') {
					Image rice = new Image(resources.rice());
					rice.setPixelSize(riceSize, riceSize);
					rice.getElement().getStyle().setPosition(Position.ABSOLUTE);
					rice.getElement().getStyle().setLeft((c * tileSize) + riceOffset, Unit.PX);
					rice.getElement().getStyle().setTop((r * tileSize) + riceOffset, Unit.PX);

					mainCanvas.add(rice);
					mapWidgets.add(rice);
					riceGrid[r][c] = rice;
					totalRiceRemaining++;
				} else if (cell == '*') {
					Image fruitItem = new Image(resources.fruit());
					fruitItem.setPixelSize(fruitSize, fruitSize);
					fruitItem.getElement().getStyle().setPosition(Position.ABSOLUTE);
					fruitItem.getElement().getStyle().setLeft((c * tileSize) + fruitOffset, Unit.PX);
					fruitItem.getElement().getStyle().setTop((r * tileSize) + fruitOffset, Unit.PX);

					mainCanvas.add(fruitItem);
					mapWidgets.add(fruitItem);
					fruitGrid[r][c] = fruitItem;
				}
			}
		}
	}

	private void checkItemCollision() {
		int col = Math.round((float) pacMan.getRightStep() / 30);
		int row = Math.round((float) pacMan.getTopStep() / 30);

		if (row >= 0 && row < MAP_1.length && col >= 0 && col < MAP_1[0].length()) {
			// 1. Process Rice Collision
			Image riceTile = riceGrid[row][col];
			if (riceTile != null) {
				mainCanvas.remove(riceTile);
				mapWidgets.remove(riceTile);
				riceGrid[row][col] = null;

				score += 10;
				scoreLabel.setText("Score: " + score);
				pacMan.triggerChompAnimation(mainCanvas);

				totalRiceRemaining--;
				riceChompCounter++;

				// Win Condition: All rice cleared -> Freeze Pacman
				// Win Condition: All rice cleared -> Generate next procedural level
				// Win Condition: All rice cleared -> Generate next procedural map & preserve
				// score
				if (totalRiceRemaining <= 0) {
					if (t != null) {
						t.cancel();
					}
					despawnGhosts();

					com.google.gwt.user.client.Window.alert("Stage Cleared! Loading next map...");

					generateNextMap();
					resetPacmanGame(false); // Keeps score, renders procedural map
					return;
				}

				// Spawn ghosts after every 10 rice chomps
				if (riceChompCounter > 0 && riceChompCounter % 10 == 0) {
					spawnGhosts();
				}
				return;
			}

			// 2. Process Fruit Collision
			Image fruitTile = fruitGrid[row][col];
			if (fruitTile != null) {
				mainCanvas.remove(fruitTile);
				mapWidgets.remove(fruitTile);
				fruitGrid[row][col] = null;

				score += 50;
				scoreLabel.setText("Score: " + score);
				pacMan.triggerChompAnimation(mainCanvas);

				// Increase Pacman speed by reducing timer interval (floor limit at 150ms)
				if (timerInterval > 50) {
					timerInterval -= 125;
					if (t != null) {
						t.cancel();
						t.scheduleRepeating(timerInterval);
					}
				}
			}
		}
	}

	/**
	 * Spawns all 4 ghost types at unique, random non-brick locations.
	 */
	private void spawnGhosts() {
		despawnGhosts(); // Clear any active ghosts before spawning new ones

		List<int[]> validCells = new ArrayList<>();
		int rows = MAP_1.length;
		int cols = MAP_1[0].length();

		int pacCol = Math.round((float) pacMan.getRightStep() / 30);
		int pacRow = Math.round((float) pacMan.getTopStep() / 30);

		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				if (MAP_1[r].charAt(c) != '-') {
					// Avoid spawning directly on Pacman
					if (r != pacRow || c != pacCol) {
						validCells.add(new int[] { r, c });
					}
				}
			}
		}

		if (validCells.size() < 4)
			return;

		// Pick 4 unique locations
		Collections.shuffle(validCells);
		Ghost.GhostType[] types = Ghost.GhostType.values();

		for (int i = 0; i < 4; i++) {
			int[] pos = validCells.get(i);
			Ghost ghost = new Ghost(types[i]);
			ghost.setRightStep(pos[1] * 30);
			ghost.setTopStep(pos[0] * 30);

			mainCanvas.add(ghost.getImage());
			ghost.updatePosition();
			activeGhosts.add(ghost);
		}

		// Despawn ghosts after 5 seconds
		ghostDespawnTimer = new Timer() {
			@Override
			public void run() {
				despawnGhosts();
			}
		};
		ghostDespawnTimer.schedule(5000);
	}

	private void despawnGhosts() {
		if (ghostDespawnTimer != null) {
			ghostDespawnTimer.cancel();
		}
		for (Ghost g : activeGhosts) {
			mainCanvas.remove(g.getImage());
		}
		activeGhosts.clear();
	}

	private void checkGhostCollision() {
		int pacCol = Math.round((float) pacMan.getRightStep() / 30);
		int pacRow = Math.round((float) pacMan.getTopStep() / 30);

		for (Ghost g : activeGhosts) {
			int ghostCol = Math.round((float) g.getRightStep() / 30);
			int ghostRow = Math.round((float) g.getTopStep() / 30);

			if (pacCol == ghostCol && pacRow == ghostRow) {
				com.google.gwt.user.client.Window.alert("Game Over! Pacman was caught by a ghost.");
				resetPacmanGame(true); // Full reset: score = 0, reloads Map_1.txt
				return;
			}
		}
	}

	/**
	 * Checks if moving 1 grid step in 'dir' from the current position hits a brick
	 * ('-').
	 */
	public boolean canMove(int currentRightStep, int currentTopStep, String dir) {
		if (dir == null || dir.isEmpty()) {
			return false;
		}

		int col = currentRightStep / 30;
		int row = currentTopStep / 30;

		int targetCol = col;
		int targetRow = row;

		if (Direction.RIGHT.equalsIgnoreCase(dir)) {
			targetCol++;
		} else if (Direction.LEFT.equalsIgnoreCase(dir)) {
			targetCol--;
		} else if (Direction.UP.equalsIgnoreCase(dir)) {
			targetRow--;
		} else if (Direction.DOWN.equalsIgnoreCase(dir)) {
			targetRow++;
		}

		int maxRows = MAP_1.length;
		int maxCols = MAP_1[0].length();

		// Teleport wrapping across grid boundaries
		if (targetCol < 0) {
			targetCol = maxCols - 1;
		} else if (targetCol >= maxCols) {
			targetCol = 0;
		}

		if (targetRow < 0) {
			targetRow = maxRows - 1;
		} else if (targetRow >= maxRows) {
			targetRow = 0;
		}

		char cell = MAP_1[targetRow].charAt(targetCol);

		// Return true only if target cell is NOT a brick
		return cell != '-';
	}

	/**
	 * Generates a procedural 13x27 map featuring 4-way symmetry (left-right &
	 * top-bottom) with matching portal entry points on opposite edges.
	 */
	public void generateNextMap() {
		int rows = 13;
		int cols = 27;
		char[][] grid = new char[rows][cols];

		// 1. Fill entire grid with rice path '.'
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				grid[r][c] = '.';
			}
		}

		// 2. Set Outer Perimeter Brick Walls '-'
		for (int c = 0; c < cols; c++) {
			grid[0][c] = '-';
			grid[rows - 1][c] = '-';
		}
		for (int r = 0; r < rows; r++) {
			grid[r][0] = '-';
			grid[r][cols - 1] = '-';
		}

		// 3. Define Matching Edge Portals ('0') on Opposite Axes
		// Top and Bottom center portal pair
		grid[0][13] = '0';
		grid[rows - 1][13] = '0';

		// Left and Right side portal pairs (rows 1, 5, 7, 11)
		int[] sidePortalRows = { 1, 5, 7, 11 };
		for (int r : sidePortalRows) {
			grid[r][0] = '0';
			grid[r][cols - 1] = '0';
		}

		// 4. Procedurally Generate Top-Left Quadrant Brick Layout
		int midRow = rows / 2; // 6
		int midCol = cols / 2; // 13

		for (int r = 2; r <= midRow - 1; r += 2) {
			for (int c = 2; c <= midCol - 2; c += 3) {
				if (Math.random() > 0.25) {
					grid[r][c] = '-';
					grid[r][c + 1] = '-';
				}
			}
		}

		// Center divider blocks (top half)
		if (Math.random() > 0.4) {
			grid[2][13] = '-';
			grid[3][13] = '-';
			grid[4][13] = '-';
		}

		// 5. Apply 4-Way Symmetrical Mirroring Across Center Axes
		for (int r = 0; r <= midRow; r++) {
			for (int c = 0; c <= midCol; c++) {
				char tile = grid[r][c];
				if (tile == '0')
					continue; // Preserve outer portals

				// Mirror Horizontally
				grid[r][cols - 1 - c] = tile;
				// Mirror Vertically
				grid[rows - 1 - r][c] = tile;
				// Mirror Quadrant Diagonal
				grid[rows - 1 - r][cols - 1 - c] = tile;
			}
		}

		// 6. Ensure Portals and Spawn Points remain clear
		grid[0][13] = '0';
		grid[rows - 1][13] = '0';
		for (int r : sidePortalRows) {
			grid[r][0] = '0';
			grid[r][cols - 1] = '0';
		}

		// Clear Pacman spawn [1,1] and corners
		grid[1][1] = '.';
		grid[1][2] = '.';
		grid[2][1] = '.';

		// 7. Symmetrical Fruit Placement (*)
		grid[3][1] = '*';
		grid[3][cols - 2] = '*';
		grid[rows - 4][1] = '*';
		grid[rows - 4][cols - 2] = '*';

		// 8. Convert 2D char array back to MAP_1 String array
		MAP_1 = new String[rows];
		for (int r = 0; r < rows; r++) {
			MAP_1[r] = new String(grid[r]);
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
