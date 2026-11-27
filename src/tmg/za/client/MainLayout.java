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
import com.google.gwt.http.client.URL;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

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

/**
 * MainLayout class
 */
public class MainLayout extends Composite {

	private static MainLayoutUiBinder uiBinder = GWT.create(MainLayoutUiBinder.class);
	/* Deprecated */
	// private final FileServiceAsync fileService = GWT.create(FileService.class);

	Resources resources = GWT.create(Resources.class);
	Physics physics = new Physics();

	interface MainLayoutUiBinder extends UiBinder<Widget, MainLayout> {
	}

	public static final String BALL = "BALL";
	public static final String SPACE = "SPACE";
	public static final String SNAKE = "SNAKE";
	public static final String PACMAN = "PACMAN";

	/* Pacman */
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

	/* SpaceForce */
	private final List<Image> activeExplosions = new ArrayList<>();
	private final List<Timer> activeExplosionTimers = new ArrayList<>();
	private Timer spaceSwayTimer;
	private boolean spaceHandlersInitialized = false;
	private int extraLives = 0;
	private final HorizontalPanel lifePanel = new HorizontalPanel();
	private final List<Image> lifeCoinImages = new ArrayList<>();
	private final List<Widget> activeLifeBanners = new ArrayList<>();
	private final List<Timer> activeLifeBannerTimers = new ArrayList<>();

	@UiField
	SplitLayoutPanel splitLayout;
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
	/* Deprecated */
	// FileEditor fileEditor;
	// Login login;
	// Ball ball;
	// FileUploader upload = new FileUploader();
	GuessingGame gg;
	SpaceForce sf;
	Snake snake;
	Pacman pacMan;

	// Track the active image widget on the canvas
	private Image currentPacmanImage = null;
	Timer t;
	private int currentSpeed = 1000; // Starts at 1 step per 1000ms
	private int score = 0;
	private com.google.gwt.user.client.ui.Label scoreLabel = new com.google.gwt.user.client.ui.Label("Score: 0");
	Fruit fruit = new Fruit();

	ArrayList<Asteroid> asteroids = new ArrayList<Asteroid>();
	private static final int INITIAL_BULLETS = 10;
	private static final int[] POWER_UP_AMOUNTS = { 10, 20, 50 };

	private int bulletsRemaining = INITIAL_BULLETS;
	private int survivalSeconds = 0;
	private int asteroidsDestroyed = 0;
	private Timer survivalTimer;

	private final Label ammoLabel = new Label("Bullets: " + INITIAL_BULLETS);
	private final Label survivalLabel = new Label("Survived: 0s");
	private final Label asteroidsLabel = new Label("Asteroids: 0");

	/**
	 * Space asteroid sub-class
	 */
	private static class SpaceAsteroid {
		final Asteroid asteroid;

		SpaceAsteroid(Asteroid asteroid) {
			this.asteroid = asteroid;
		}
	}

	/**
	 * Bullet power up class
	 */
	private static class BulletPowerUp {
		final Image image;
		final int bulletBonus;
		int rightStep;
		int topStep;

		/**
		 * @param image
		 * @param bulletBonus
		 * @param rightStep
		 * @param topStep
		 */
		BulletPowerUp(Image image, int bulletBonus, int rightStep, int topStep) {
			this.image = image;
			this.bulletBonus = bulletBonus;
			this.rightStep = rightStep;
			this.topStep = topStep;
		}

		/**
		 * @param deltaX
		 */
		void moveX(int deltaX) {
			rightStep += deltaX;
			image.getElement().getStyle().setPosition(Position.ABSOLUTE);
			image.getElement().getStyle().setLeft(rightStep, Unit.PX);
			image.getElement().getStyle().setTop(topStep, Unit.PX);
			image.setVisible(true);
		}
	}

	private final List<SpaceAsteroid> spaceAsteroids = new ArrayList<>();
	private final List<BulletPowerUp> spacePowerUps = new ArrayList<>();

	/**
	 * MainLayout constructor
	 */
	public MainLayout() {
		initWidget(uiBinder.createAndBindUi(this));
		Window.addResizeHandler(event -> {
			applyHeaderViewportStyle(event.getWidth());
			if (controlPanel != null && controlPanel.isVisible() && sf != null) {
				applySpaceBackgroundStyle();
			}
		});
		// login = new Login();
		gg = new GuessingGame();
		// fileEditor = new FileEditor();
		// ball = new Ball();
		sf = new SpaceForce();
		snake = new Snake();
		pacMan = new Pacman();

		// mainPanel.add(login.getPbLogin());
		mainPanel.add(gg.getPbGuess());
		// mainPanel.add(fileEditor.getPbFileEditor());
		// mainPanel.add(ball.getPbBall());
		mainPanel.add(sf.getPbSpaceForce());
		mainPanel.add(snake.getPbSnake());
		mainPanel.add(pacMan.getPbPacman());
		applyHeaderViewportStyle(Window.getClientWidth());

		/*
		 * Deprecated login.getPbLogin().addClickHandler(new ClickHandler() {
		 * 
		 * @Override public void onClick(ClickEvent event) { if (t != null) {
		 * t.cancel(); } login.center(); login.setGlassEnabled(true);
		 * taCanvas.setVisible(false); tbDocument.setVisible(false);
		 * buttonPanel.setVisible(false); tbDocument.setText("");
		 * tbDocument.setReadOnly(true); mainCanvas.remove(ball.getImage());
		 * mainCanvas.remove(sf.getImage()); mainCanvas.remove(snake.getImage());
		 * mainCanvas.remove(fruit.getImage()); mainCanvas.remove(pacMan.getImage());
		 * clearPacmanAssets(); clearAsteroids(); // ball.setLive(false);
		 * controlPanel.setVisible(false); setEnabled(false, true); } });
		 */
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
				// mainCanvas.remove(ball.getImage());
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

		/*
		 * Deprecated ball.getPbBall().addClickHandler(new ClickHandler() {
		 * 
		 * @Override public void onClick(ClickEvent event) { if (t != null) {
		 * t.cancel(); } canvasPanel.setVisible(false); buttonPanel.setVisible(false);
		 * setupControlPanel(BALL); controlPanel.setVisible(true);
		 * mainCanvas.add(ball.getImage().asWidget()); // ball.setLive(true);
		 * mainCanvas.remove(sf.getImage()); mainCanvas.remove(snake.getImage());
		 * mainCanvas.remove(fruit.getImage()); mainCanvas.remove(pacMan.getImage());
		 * clearPacmanAssets(); clearAsteroids(); setEnabled(true, true);
		 * mainCanvas.getParent().getElement().setAttribute("style",
		 * "position: absolute; inset: 0px;background-position:center;background-repeat:no-repeat"
		 * ); } });
		 */
		sf.getPbSpaceForce().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				canvasPanel.setVisible(false);
				buttonPanel.setVisible(false);
				controlPanel.setVisible(true);
				setupControlPanel(SPACE);
				mainCanvas.remove(snake.getImage());
				mainCanvas.remove(fruit.getImage());
				mainCanvas.remove(pacMan.getImage());
				setEnabled(true, false);
				clearPacmanAssets();
				resetSpaceGame(true);

				com.google.gwt.core.client.Scheduler.get().scheduleDeferred(() -> {
					if (sf != null && sf.getPbKeyboard() != null && sf.getPbKeyboard().isAttached()) {
						sf.getPbKeyboard().setFocus(true);
					}
				});
			}
		});
		snake.getPbSnake().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (t != null) {
					t.cancel();
				}
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
							if (hLeft == cLeft && hTop == cTop) {
								t.cancel(); // Pause game loop while popup is open
								int finalScore = score;

								// 1. Clear out tail children and reset head position
								snake.reset();

								// 2. Clear out score tracking variables back to zero
								score = 0;
								scoreLabel.setText("Score: 0");

								// 3. Re-initialize baseline starting speed delay
								currentSpeed = 1000;

								// 4. Show Game Over popup with Share & Close
								showSnakeGameOverDialog(finalScore);
								return;
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
				// mainCanvas.remove(ball.getImage());
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
			 * @return Image
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
				// mainCanvas.remove(ball.getImage());
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

	}

	/**
	 * @param finalScore
	 */
	private void showSnakeGameOverDialog(int finalScore) {
		com.google.gwt.user.client.ui.DialogBox gameOverBox = new com.google.gwt.user.client.ui.DialogBox(false, true);
		gameOverBox.setText("Game Over");
		gameOverBox.setGlassEnabled(true);

		com.google.gwt.user.client.ui.VerticalPanel content = new com.google.gwt.user.client.ui.VerticalPanel();
		content.setSpacing(8);

		content.add(new Label("You bit your tail! Final Score: " + finalScore));

		String shareText = "I scored " + finalScore + " points in Snake on Ben's Java GWT Showcase! (PC)";
		String currentUrl = Window.Location.getHref();
		String encodedText = com.google.gwt.http.client.URL.encodeQueryString(shareText);
		String encodedUrl = com.google.gwt.http.client.URL.encodeQueryString(currentUrl);

		com.google.gwt.user.client.ui.Button shareButton = new com.google.gwt.user.client.ui.Button("Share");
		shareButton.addClickHandler(event -> {
			com.google.gwt.user.client.ui.PopupPanel sharePopup = new com.google.gwt.user.client.ui.PopupPanel(true);
			com.google.gwt.user.client.ui.VerticalPanel linksBox = new com.google.gwt.user.client.ui.VerticalPanel();
			linksBox.setSpacing(6);

			com.google.gwt.user.client.ui.Anchor shareWhatsApp = new com.google.gwt.user.client.ui.Anchor("WhatsApp",
					true, "https://wa.me/?text="
							+ com.google.gwt.http.client.URL.encodeQueryString(shareText + " " + currentUrl));
			shareWhatsApp.getElement().setAttribute("target", "_blank");
			shareWhatsApp.getElement().setAttribute("rel", "noopener noreferrer");

			com.google.gwt.user.client.ui.Anchor shareX = new com.google.gwt.user.client.ui.Anchor("X", true,
					"https://twitter.com/intent/tweet?text=" + encodedText + "&url=" + encodedUrl);
			shareX.getElement().setAttribute("target", "_blank");
			shareX.getElement().setAttribute("rel", "noopener noreferrer");

			linksBox.add(shareWhatsApp);
			linksBox.add(shareX);
			sharePopup.setWidget(linksBox);
			sharePopup.showRelativeTo(shareButton);
		});

		com.google.gwt.user.client.ui.Button closeButton = new com.google.gwt.user.client.ui.Button("Close");
		closeButton.addClickHandler(event -> {
			gameOverBox.hide();
			if (t != null) {
				t.scheduleRepeating(currentSpeed);
			}
		});

		HorizontalPanel actions = new HorizontalPanel();
		actions.setSpacing(8);
		actions.add(shareButton);
		actions.add(closeButton);
		content.add(actions);

		gameOverBox.setWidget(content);
		gameOverBox.center();
		gameOverBox.show();
	}

	/**
	 * 
	 */
	protected void clearAsteroids() {
		if (survivalTimer != null) {
			survivalTimer.cancel();
			survivalTimer = null;
		}
		for (Asteroid ast : asteroids) {
			ast.getImage().removeFromParent();
		}
		asteroids.clear();
		spaceAsteroids.clear();

		for (BulletPowerUp pu : spacePowerUps) {
			pu.image.removeFromParent();
		}
		spacePowerUps.clear();

		for (Timer timer : activeExplosionTimers) {
			timer.cancel();
		}
		activeExplosionTimers.clear();

		for (Image exp : activeExplosions) {
			exp.removeFromParent();
		}
		activeExplosions.clear();

		stopSpaceSwayTimer();

		for (Timer timer : activeLifeBannerTimers) {
			timer.cancel();
		}
		activeLifeBannerTimers.clear();

		for (Widget banner : activeLifeBanners) {
			banner.removeFromParent();
		}
		activeLifeBanners.clear();
	}

	/**
	 * 
	 */
	private void spawnSpaceWave() {
		int rightGap = 0;

		for (int group = 0; group < 3; group++) {
			for (int slot = 0; slot < 3; slot++) {
				int xPos = 735 + rightGap;
				int yPos = generateGap();

				Asteroid ast = new Asteroid();
				ast.setRightStep(xPos);
				ast.setTopStep(yPos);
				ast.getImage().getElement().getStyle().setPosition(Position.ABSOLUTE);
				mainCanvas.add(ast.getImage());
				asteroids.add(ast);
				spaceAsteroids.add(new SpaceAsteroid(ast));
			}

			rightGap += 140 * 3;
		}
	}

	/**
	 * 
	 */
	private void spawnTimedPowerUp() {
		int bonus = POWER_UP_AMOUNTS[(int) (Math.random() * POWER_UP_AMOUNTS.length)];
		int xPos = 735;
		int yPos = generateGap();

		Image puImg = new Image(resources.powerUp());
		puImg.setPixelSize(32, 32);
		puImg.getElement().getStyle().setPosition(Position.ABSOLUTE);
		puImg.getElement().getStyle().setLeft(xPos, Unit.PX);
		puImg.getElement().getStyle().setTop(yPos, Unit.PX);
		mainCanvas.add(puImg);
		spacePowerUps.add(new BulletPowerUp(puImg, bonus, xPos, yPos));
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
	private void updateSpaceHud() {
		ammoLabel.setText("Bullets: " + bulletsRemaining);
		survivalLabel.setText("Survived: " + survivalSeconds + "s");
		asteroidsLabel.setText("Asteroids: " + asteroidsDestroyed);
	}

	/**
	 * 
	 */
	private void startSpaceLoops() {
		if (t != null) {
			t.cancel();
		}
		if (survivalTimer != null) {
			survivalTimer.cancel();
		}

		survivalTimer = new Timer() {
			@Override
			public void run() {
				survivalSeconds++;
				updateSpaceHud();
				if (survivalSeconds > 0 && survivalSeconds % 30 == 0) {
					spawnTimedPowerUp();
				}
			}
		};
		survivalTimer.scheduleRepeating(1000);

		t = new Timer() {
			@Override
			public void run() {
				redrawSpace();
			}
		};
		t.scheduleRepeating(100); // Smooth step ticks; base force scaled for ~10 ticks/sec

		startSpaceSwayTimer();

	}

	/**
	 * @param restartLoops
	 */
	private void resetSpaceGame(boolean restartLoops) {
		clearAsteroids();
		bulletsRemaining = INITIAL_BULLETS;
		survivalSeconds = 0;
		asteroidsDestroyed = 0;
		extraLives = 0;
		clearLifeCoinsFromHud();
		updateSpaceHud();

		sf.setRightStep(0);
		sf.setTopStep(0);
		sf.getImage().getElement().getStyle().setPosition(Position.ABSOLUTE);
		sf.getImage().getElement().getStyle().setLeft(0, Unit.PX);
		sf.getImage().getElement().getStyle().setTop(0, Unit.PX);

		if (sf.getImage().getParent() == null) {
			mainCanvas.add(sf.getImage().asWidget());
		}
		sf.setDistance(mainCanvas.getAbsoluteLeft());
		spawnSpaceWave();

		if (restartLoops) {
			startSpaceLoops();
		}

	}

	/**
	 * @param target
	 */
	private void removeSpaceAsteroidRef(Asteroid target) {
		for (int i = spaceAsteroids.size() - 1; i >= 0; i--) {
			if (spaceAsteroids.get(i).asteroid == target) {
				spaceAsteroids.remove(i);
			}
		}
	}

	/**
	 * @param pu
	 * @return true or false
	 */
	private boolean isShipTouchingPowerUp(BulletPowerUp pu) {
		int shipLeft = sf.getImage().getAbsoluteLeft();
		int shipTop = sf.getImage().getAbsoluteTop();
		int shipRight = shipLeft + sf.getImage().getOffsetWidth();
		int shipBottom = shipTop + sf.getImage().getOffsetHeight();

		int puLeft = pu.image.getAbsoluteLeft();
		int puTop = pu.image.getAbsoluteTop();
		int puRight = puLeft + pu.image.getOffsetWidth();
		int puBottom = puTop + pu.image.getOffsetHeight();

		return shipLeft < puRight && shipRight > puLeft && shipTop < puBottom && shipBottom > puTop;
	}

	/**
	 * 
	 */
	private void redrawSpace() {
		applySpaceBackgroundStyle();
		sf.setDistance(sf.getDistance() - physics.getSpace());

		int baseStep = Math.max(1, physics.getForce(3, false, false) / 10);

		for (int i = spaceAsteroids.size() - 1; i >= 0; i--) {
			SpaceAsteroid entry = spaceAsteroids.get(i);
			int step = baseStep * 5;
			entry.asteroid.moveX(-step);

			if (entry.asteroid.getImage().getAbsoluteLeft() < 165) {
				entry.asteroid.getImage().removeFromParent();
				asteroids.remove(entry.asteroid);
				spaceAsteroids.remove(i);
				continue;
			}

			if (physics.checkCollision(sf, entry.asteroid)) {
				if (extraLives > 0) {
					respawnAfterExtraLife();
					return;
				}
				if (t != null) {
					t.cancel();
				}
				if (survivalTimer != null) {
					survivalTimer.cancel();
				}
				stopSpaceSwayTimer();
				int finalSeconds = survivalSeconds;
				sf.getImage().removeFromParent();
				showSpaceGameOverDialog(finalSeconds);
				return;
			}
		}

		int powerUpStep = baseStep * 5;
		for (int i = spacePowerUps.size() - 1; i >= 0; i--) {
			BulletPowerUp pu = spacePowerUps.get(i);
			pu.moveX(-powerUpStep);

			if (pu.image.getAbsoluteLeft() < 165) {
				pu.image.removeFromParent();
				spacePowerUps.remove(i);
				continue;
			}

			if (isShipTouchingPowerUp(pu)) {
				bulletsRemaining += pu.bulletBonus;
				updateSpaceHud();
				pu.image.removeFromParent();
				spacePowerUps.remove(i);
			}
		}

		if (spaceAsteroids.isEmpty() && spacePowerUps.isEmpty()) {
			spawnSpaceWave();
		}
	}

	/**
	 * @param absLeft
	 * @param absTop
	 * @param astWidth
	 * @param astHeight
	 */
	private void spawnExplosion(int absLeft, int absTop, int astWidth, int astHeight) {
		int size = 96;
		int effectiveWidth = astWidth > 0 ? astWidth : 250;
		int effectiveHeight = astHeight > 0 ? astHeight : 250;

		int canvasLeft = mainCanvas.getAbsoluteLeft();
		int canvasTop = mainCanvas.getAbsoluteTop();

		int centerX = (absLeft - canvasLeft) + (effectiveWidth / 2);
		int centerY = (absTop - canvasTop) + (effectiveHeight / 2);
		int left = centerX - (size / 2);
		int top = centerY - (size / 2);

		Image explosion = new Image(resources.explosion());
		explosion.setPixelSize(size, size);
		explosion.getElement().getStyle().setPosition(Position.ABSOLUTE);
		explosion.getElement().getStyle().setLeft(left, Unit.PX);
		explosion.getElement().getStyle().setTop(top, Unit.PX);
		explosion.getElement().getStyle().setProperty("pointerEvents", "none");
		explosion.getElement().getStyle().setProperty("transition", "transform 1.8s ease-out, opacity 2s ease-out");
		explosion.getElement().getStyle().setProperty("transform", "scale(0.65)");
		explosion.getElement().getStyle().setProperty("opacity", "1");

		mainCanvas.add(explosion);
		activeExplosions.add(explosion);

		// Kick off transition on next frame so CSS transition animates smoothly
		com.google.gwt.core.client.Scheduler.get().scheduleDeferred(() -> {
			if (explosion.isAttached()) {
				explosion.getElement().getStyle().setProperty("transform", "scale(1.35)");
				explosion.getElement().getStyle().setProperty("opacity", "0");
			}
		});

		Timer removeTimer = new Timer() {
			@Override
			public void run() {
				explosion.removeFromParent();
				activeExplosions.remove(explosion);
				activeExplosionTimers.remove(this);
			}
		};
		activeExplosionTimers.add(removeTimer);
		removeTimer.schedule(2000);
	}

	/**
	 * @param secondsSurvived
	 */
	private void showSpaceGameOverDialog(int secondsSurvived) {
		DialogBox gameOverBox = new DialogBox(false, true);
		gameOverBox.setText("Game Over");
		gameOverBox.setGlassEnabled(true);

		VerticalPanel content = new VerticalPanel();
		content.setSpacing(8);

		content.add(new Label("Hit by an asteroid! Time survived: " + secondsSurvived + "s"));

		String shareText = "I survived " + secondsSurvived + "s and exploded " + asteroidsDestroyed
				+ " asteroids in Space Force on Ben's Java GWT Showcase! (PC)";
		String currentUrl = Window.Location.getHref();
		String encodedText = URL.encodeQueryString(shareText);
		String encodedUrl = URL.encodeQueryString(currentUrl);

		Button shareButton = new Button("Share");
		shareButton.addClickHandler(event -> {
			PopupPanel sharePopup = new PopupPanel(true);
			VerticalPanel linksBox = new VerticalPanel();
			linksBox.setSpacing(6);

			Anchor shareWhatsApp = new Anchor("WhatsApp", true,
					"https://wa.me/?text=" + URL.encodeQueryString(shareText + " " + currentUrl));
			shareWhatsApp.getElement().setAttribute("target", "_blank");
			shareWhatsApp.getElement().setAttribute("rel", "noopener noreferrer");

			Anchor shareX = new Anchor("X", true,
					"https://twitter.com/intent/tweet?text=" + encodedText + "&url=" + encodedUrl);
			shareX.getElement().setAttribute("target", "_blank");
			shareX.getElement().setAttribute("rel", "noopener noreferrer");

			linksBox.add(shareWhatsApp);
			linksBox.add(shareX);
			sharePopup.setWidget(linksBox);
			sharePopup.showRelativeTo(shareButton);
		});

		Button closeButton = new Button("Close");
		closeButton.addClickHandler(event -> {
			gameOverBox.hide();
			resetSpaceGame(true);
			com.google.gwt.core.client.Scheduler.get().scheduleDeferred(() -> {
				if (sf != null && sf.getPbKeyboard() != null && sf.getPbKeyboard().isAttached()) {
					sf.getPbKeyboard().setFocus(true);
				}
			});
		});

		HorizontalPanel actions = new HorizontalPanel();
		actions.setSpacing(8);
		actions.add(shareButton);
		actions.add(closeButton);
		content.add(actions);

		gameOverBox.setWidget(content);
		gameOverBox.center();
		gameOverBox.show();
	}

	/**
	 * 
	 */
	private void startSpaceSwayTimer() {
		stopSpaceSwayTimer();
		sf.resetVerticalMotion();
		spaceSwayTimer = new Timer() {
			@Override
			public void run() {
				if (sf != null && sf.getImage() != null && sf.getImage().isAttached()) {
					sf.updateSway(0.025);
				}
			}
		};
		spaceSwayTimer.scheduleRepeating(25);
	}

	/**
	 * 
	 */
	private void stopSpaceSwayTimer() {
		if (spaceSwayTimer != null) {
			spaceSwayTimer.cancel();
			spaceSwayTimer = null;
		}
		if (sf != null) {
			sf.resetVerticalMotion();
		}
	}

	/**
	 * 
	 */
	private void addLifeCoinToHud() {
		Image coin = new Image(resources.extraLife());
		coin.setPixelSize(28, 28);
		coin.getElement().getStyle().setProperty("marginLeft", "3px");
		coin.getElement().getStyle().setProperty("marginRight", "3px");
		lifeCoinImages.add(coin);
		lifePanel.add(coin);
	}

	/**
	 * 
	 */
	private void removeLifeCoinFromHud() {
		if (!lifeCoinImages.isEmpty()) {
			Image coin = lifeCoinImages.remove(lifeCoinImages.size() - 1);
			coin.removeFromParent();
		}
	}

	/**
	 * 
	 */
	private void clearLifeCoinsFromHud() {
		for (Image coin : lifeCoinImages) {
			coin.removeFromParent();
		}
		lifeCoinImages.clear();
		lifePanel.clear();
	}

	/**
	 * 
	 */
	private void spawnExtraLifeBanner() {
		Label banner = new Label("Extra Life");
		banner.getElement().getStyle().setPosition(Position.ABSOLUTE);
		banner.getElement().getStyle().setLeft(140, Unit.PX);
		banner.getElement().getStyle().setTop(140, Unit.PX);
		banner.getElement().getStyle().setProperty("pointerEvents", "none");
		banner.getElement().getStyle().setProperty("fontSize", "36px");
		banner.getElement().getStyle().setProperty("fontWeight", "bold");
		banner.getElement().getStyle().setProperty("fontFamily", "sans-serif");
		banner.getElement().getStyle().setProperty("color", "#FFD54F");
		banner.getElement().getStyle().setProperty("textShadow", "0 2px 10px rgba(0, 0, 0, 0.85)");
		banner.getElement().getStyle().setProperty("transition", "transform 2s ease-in-out, opacity 2s ease-in-out");
		banner.getElement().getStyle().setProperty("transform", "translateY(25px) scale(0.9)");
		banner.getElement().getStyle().setProperty("opacity", "0");

		mainCanvas.add(banner);
		activeLifeBanners.add(banner);

		// Transition in, glide across, and fade out over the 2-second window
		com.google.gwt.core.client.Scheduler.get().scheduleDeferred(() -> {
			if (banner.isAttached()) {
				banner.getElement().getStyle().setProperty("transform", "translateY(-35px) scale(1.12)");
				banner.getElement().getStyle().setProperty("opacity", "1");
			}
		});

		com.google.gwt.core.client.Scheduler.get().scheduleFixedDelay(() -> {
			if (banner.isAttached()) {
				banner.getElement().getStyle().setProperty("opacity", "0");
			}
			return false;
		}, 1200);

		Timer removeTimer = new Timer() {
			@Override
			public void run() {
				banner.removeFromParent();
				activeLifeBanners.remove(banner);
				activeLifeBannerTimers.remove(this);
			}
		};
		activeLifeBannerTimers.add(removeTimer);
		removeTimer.schedule(2000);
	}

	/**
	 * 
	 */
	private void respawnAfterExtraLife() {
		extraLives--;
		removeLifeCoinFromHud();

		// Clear active wave/powerups/explosions/sway while preserving survivalSeconds &
		// asteroidsDestroyed
		if (t != null) {
			t.cancel();
		}
		for (Asteroid ast : asteroids) {
			ast.getImage().removeFromParent();
		}
		asteroids.clear();
		spaceAsteroids.clear();

		for (BulletPowerUp pu : spacePowerUps) {
			pu.image.removeFromParent();
		}
		spacePowerUps.clear();

		for (Timer timer : activeExplosionTimers) {
			timer.cancel();
		}
		activeExplosionTimers.clear();

		for (Image exp : activeExplosions) {
			exp.removeFromParent();
		}
		activeExplosions.clear();

		stopSpaceSwayTimer();

		bulletsRemaining = INITIAL_BULLETS;
		updateSpaceHud();

		sf.setRightStep(0);
		sf.setTopStep(0);
		sf.getImage().getElement().getStyle().setPosition(Position.ABSOLUTE);
		sf.getImage().getElement().getStyle().setLeft(0, Unit.PX);
		sf.getImage().getElement().getStyle().setTop(0, Unit.PX);

		if (sf.getImage().getParent() == null) {
			mainCanvas.add(sf.getImage().asWidget());
		}
		sf.setDistance(mainCanvas.getAbsoluteLeft());
		spawnSpaceWave();
		startSpaceLoops();

		com.google.gwt.core.client.Scheduler.get().scheduleDeferred(() -> {
			if (sf != null && sf.getPbKeyboard() != null && sf.getPbKeyboard().isAttached()) {
				sf.getPbKeyboard().setFocus(true);
			}
		});
	}

	/**
	 * @param finalScore
	 */
	private void showPacmanGameOverDialog(int finalScore) {
		DialogBox gameOverBox = new DialogBox(false, true);
		gameOverBox.setText("Game Over");
		gameOverBox.setGlassEnabled(true);

		VerticalPanel content = new VerticalPanel();
		content.setSpacing(8);

		content.add(new Label("Pacman was caught by a ghost! Final Score: " + finalScore));

		String shareText = "I scored " + finalScore + " points in Pacman on Ben's Java GWT Showcase! (PC)";
		String currentUrl = Window.Location.getHref();
		String encodedText = URL.encodeQueryString(shareText);
		String encodedUrl = URL.encodeQueryString(currentUrl);

		Button shareButton = new Button("Share");
		shareButton.addClickHandler(event -> {
			PopupPanel sharePopup = new PopupPanel(true);
			VerticalPanel linksBox = new VerticalPanel();
			linksBox.setSpacing(6);

			Anchor shareWhatsApp = new Anchor("WhatsApp", true,
					"https://wa.me/?text=" + URL.encodeQueryString(shareText + " " + currentUrl));
			shareWhatsApp.getElement().setAttribute("target", "_blank");
			shareWhatsApp.getElement().setAttribute("rel", "noopener noreferrer");

			Anchor shareX = new Anchor("X", true,
					"https://twitter.com/intent/tweet?text=" + encodedText + "&url=" + encodedUrl);
			shareX.getElement().setAttribute("target", "_blank");
			shareX.getElement().setAttribute("rel", "noopener noreferrer");

			linksBox.add(shareWhatsApp);
			linksBox.add(shareX);
			sharePopup.setWidget(linksBox);
			sharePopup.showRelativeTo(shareButton);
		});

		Button closeButton = new Button("Close");
		closeButton.addClickHandler(event -> gameOverBox.hide());

		HorizontalPanel actions = new HorizontalPanel();
		actions.setSpacing(8);
		actions.add(shareButton);
		actions.add(closeButton);
		content.add(actions);

		gameOverBox.setWidget(content);
		gameOverBox.center();
		gameOverBox.show();
	}

	/**
	 * Redraw Pacman
	 */
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
	 * @param newDirection
	 */
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

	/**
	 * 
	 */
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

	/**
	 * 
	 */
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

	/**
	 * 
	 */
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

	/**
	 * 
	 */
	private void despawnGhosts() {
		if (ghostDespawnTimer != null) {
			ghostDespawnTimer.cancel();
		}
		for (Ghost g : activeGhosts) {
			mainCanvas.remove(g.getImage());
		}
		activeGhosts.clear();
	}

	/**
	 * 
	 */
	private void checkGhostCollision() {
		int pacCol = Math.round((float) pacMan.getRightStep() / 30);
		int pacRow = Math.round((float) pacMan.getTopStep() / 30);

		for (Ghost g : activeGhosts) {
			int ghostCol = Math.round((float) g.getRightStep() / 30);
			int ghostRow = Math.round((float) g.getTopStep() / 30);

			if (pacCol == ghostCol && pacRow == ghostRow) {
				int finalScore = score;
				resetPacmanGame(true); // Full reset: score = 0, reloads Map_1.txt
				showPacmanGameOverDialog(finalScore);
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
	 * Deprecated File Editor
	 */
	/*
	 * private void setUpEditorButtonHandlers() {
	 * 
	 * buttonPanel.add(fileEditor.getNewButton());
	 * buttonPanel.add(fileEditor.getEditButton());
	 * buttonPanel.add(fileEditor.getSaveButton());
	 * buttonPanel.add(fileEditor.getLoadButton());
	 * 
	 * ClickHandler openHandler = new ClickHandler() {
	 * 
	 * @Override public void onClick(ClickEvent event) { if (t != null) {
	 * t.cancel(); } taCanvas.setValue(""); taCanvas.setVisible(true);
	 * tbDocument.setVisible(true); upload.getOpenButton().setEnabled(true);
	 * canvasPanel.setVisible(true); buttonPanel.setVisible(true); upload.center();
	 * upload.setGlassEnabled(true); tbDocument.setReadOnly(true);
	 * mainCanvas.remove(ball.getImage()); mainCanvas.remove(sf.getImage());
	 * clearAsteroids(); controlPanel.setVisible(false); setEnabled(false, true); }
	 * }; fileEditor.getPbFileEditor().addClickHandler(openHandler);
	 * fileEditor.getLoadButton().addClickHandler(openHandler);
	 * upload.getCloseButton().addClickHandler(new ClickHandler() {
	 * 
	 * @Override public void onClick(ClickEvent event) {
	 * taCanvas.setValue(upload.getData()); tbDocument.setVisible(true);
	 * tbDocument.setValue(upload.getUpload().getFilename());
	 * tbDocument.setReadOnly(true); upload.hide(); } });
	 * fileEditor.getNewButton().addClickHandler(new ClickHandler() {
	 * 
	 * @Override public void onClick(ClickEvent event) {
	 * tbDocument.setReadOnly(false); String filename =
	 * upload.getUpload().getFilename(); if (filename == null ||
	 * filename.trim().isEmpty()) { tbDocument.setValue("Enter path here"); }
	 * taCanvas.setValue("");
	 * 
	 * } }); fileEditor.getEditButton().addClickHandler(new ClickHandler() {
	 * 
	 * @Override public void onClick(ClickEvent event) {
	 * taCanvas.setReadOnly(false); tbDocument.setReadOnly(true); } });
	 * fileEditor.getSaveButton().addClickHandler(new ClickHandler() {
	 * 
	 * @Override public void onClick(ClickEvent event) {
	 * fileService.saveFile(tbDocument.getValue(), taCanvas.getValue(), new
	 * AsyncCallback<String>() {
	 * 
	 * @Override public void onFailure(Throwable caught) {
	 * Window.alert(caught.getLocalizedMessage()); }
	 * 
	 * @Override public void onSuccess(String result) { taCanvas.setReadOnly(true);
	 * tbDocument.setReadOnly(true); Window.alert(result); } });
	 * 
	 * } });
	 * 
	 * }
	 */

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

	/**
	 * @return true or false
	 */
	private native boolean isBrowserFullScreen() /*-{
		var doc = $doc;
		var apiFull = Boolean(doc.fullscreenElement
				|| doc.webkitFullscreenElement || doc.mozFullScreenElement
				|| doc.msFullscreenElement);
		if (apiFull) {
			return true;
		}
		var win = $wnd;
		var scr = win.screen;
		if (scr && scr.height && win.innerHeight) {
			return win.innerHeight >= scr.height * 0.92
					&& win.innerWidth >= scr.width * 0.95;
		}
		return false;
	}-*/;

	/**
	 * 
	 */
	private void applySpaceBackgroundStyle() {
		String bgPosY = isBrowserFullScreen() ? "125px" : "50%";
		mainCanvas.getParent().getElement().setAttribute("style", "position: absolute; inset: 0px;background-position:"
				+ sf.getDistance() + "px " + bgPosY + ";background-repeat:repeat-x;");
	}

	/**
	 * @param clientWidth
	 */
	private void applyHeaderViewportStyle(int clientWidth) {
		boolean mobile = clientWidth <= 768;
		String fontSize = mobile ? "24px" : "100px";
		boolean enableCanvasGames = !mobile;

		if (sf != null && sf.getPbSpaceForce() != null) {
			sf.getPbSpaceForce().setEnabled(enableCanvasGames);
			sf.getPbSpaceForce().setTitle(mobile ? "Available on desktop" : "");
		}
		if (snake != null && snake.getPbSnake() != null) {
			snake.getPbSnake().setEnabled(enableCanvasGames);
			snake.getPbSnake().setTitle(mobile ? "Available on desktop" : "");
		}
		if (pacMan != null && pacMan.getPbPacman() != null) {
			pacMan.getPbPacman().setEnabled(enableCanvasGames);
			pacMan.getPbPacman().setTitle(mobile ? "Available on desktop" : "");
		}
		headerLabel.getElement().setAttribute("style",
				"font-weight: bold;font-size:" + fontSize + ";text-align:center;");

		if (splitLayout == null || mainPanel == null) {
			return;
		}

		try {
			splitLayout.setWidgetSize(mainPanel, mobile ? 165 : 360);
			if (controlPanel != null) {
				splitLayout.setWidgetSize(controlPanel, mobile ? 90 : 360);
			}

			String mainWidth = mobile ? "150px" : "";
			String itemWidth = mobile ? "110px" : "220px";
			String parentWidth = mobile ? "115px" : "";

			mainPanel.setWidth(mainWidth);
			mainPanel.getElement().getStyle().setProperty("maxWidth", mainWidth);
			mainPanel.getElement().getStyle().setProperty("boxSizing", "border-box");

			for (Widget w : mainPanel) {
				w.setWidth(itemWidth);
				w.getElement().getStyle().setProperty("width", itemWidth);
				w.getElement().getStyle().setProperty("minWidth", mobile ? "0px" : "");
				w.getElement().getStyle().setProperty("maxWidth", mobile ? itemWidth : "");
				w.getElement().getStyle().setProperty("boxSizing", mobile ? "border-box" : "");
				w.getElement().getStyle().setProperty("whiteSpace", mobile ? "nowrap" : "");
				w.getElement().getStyle().setProperty("overflow", mobile ? "hidden" : "");
				w.getElement().getStyle().setProperty("textOverflow", mobile ? "ellipsis" : "");

				if (w.getParent() != null) {
					w.getParent().getElement().getStyle().setProperty("width", parentWidth);
					w.getParent().getElement().getStyle().setProperty("boxSizing", mobile ? "border-box" : "");
					w.getParent().getElement().getStyle().setProperty("overflow", mobile ? "hidden" : "");
				}
			}

			if (controlPanel != null) {
				controlPanel.setWidth(mobile ? "85px" : "");
				controlPanel.getElement().getStyle().setProperty("boxSizing", mobile ? "border-box" : "");
				for (Widget w : controlPanel) {
					w.getElement().getStyle().setProperty("maxWidth", mobile ? "80px" : "");
					w.getElement().getStyle().setProperty("boxSizing", mobile ? "border-box" : "");
					w.getElement().getStyle().setProperty("overflow", mobile ? "hidden" : "");
				}
			}

			com.google.gwt.core.client.Scheduler.get().scheduleDeferred(() -> {
				if (splitLayout != null) {
					splitLayout.forceLayout();
				}
			});
		} catch (Exception ex) {
			GWT.log("Error applying viewport layout styles", ex);
		}
	}

	/**
	 * 
	 */
	private void setupControlPanel(String mode) {
		controlPanel.clear();
		/*
		 * Deprecated if (mode.equalsIgnoreCase(BALL)) {
		 * controlPanel.add(ball.getUpButton()); HorizontalPanel hp = new
		 * HorizontalPanel(); hp.add(ball.getLeftButton());
		 * hp.add(ball.getDownButton()); hp.add(ball.getRightButton());
		 * controlPanel.add(hp); controlPanel.add(ball.getPbKeyboard());
		 * controlPanel.add(ball.getTbFriction());
		 * controlPanel.setCellHorizontalAlignment(hp,
		 * HasHorizontalAlignment.ALIGN_CENTER);
		 * controlPanel.setCellHorizontalAlignment(ball.getUpButton(),
		 * HasHorizontalAlignment.ALIGN_CENTER); } else
		 */

		if (mode.equalsIgnoreCase(SPACE)) {
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

			if (!spaceHandlersInitialized) {
				sf.getUpButton().addMouseDownHandler(event -> sf.setHoldingUp(true));
				sf.getUpButton().addMouseUpHandler(event -> sf.setHoldingUp(false));
				sf.getUpButton().addMouseOutHandler(event -> sf.setHoldingUp(false));

				sf.getDownButton().addMouseDownHandler(event -> sf.setHoldingDown(true));
				sf.getDownButton().addMouseUpHandler(event -> sf.setHoldingDown(false));
				sf.getDownButton().addMouseOutHandler(event -> sf.setHoldingDown(false));

				sf.getPbKeyboard().addKeyDownHandler(event -> {
					if (event.getNativeKeyCode() == KeyCodes.KEY_UP) {
						sf.setHoldingUp(true);
					} else if (event.getNativeKeyCode() == KeyCodes.KEY_DOWN) {
						sf.setHoldingDown(true);
					}
				});

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
							sf.setHoldingUp(false);
						}
						if (event.getNativeKeyCode() == KeyCodes.KEY_DOWN) {
							sf.setHoldingDown(false);
						}
						if (event.getNativeKeyCode() == KeyCodes.KEY_SPACE) {
							if (bulletsRemaining <= 0) {
								return; // Out of ammo: navigate with UP/DOWN until picking up a power-up
							}
							bulletsRemaining--;
							updateSpaceHud();

							Bullet bullet = new Bullet();
							bullet.setTopStep(sf.getTopStep());
							mainCanvas.add(bullet.getImage().asWidget());
							Timer tBullet = new Timer() {
								@Override
								public void run() {
									if (sf.getImage().isAttached()) {
										bullet.moveX(physics.getForce(bullet.getMass(), false, false));
									}
									for (int i = asteroids.size() - 1; i >= 0; i--) {
										Asteroid asteroid = asteroids.get(i);
										if (physics.checkCollision(bullet, asteroid)) {
											int hitLeft = asteroid.getImage().getAbsoluteLeft();
											int hitTop = asteroid.getImage().getAbsoluteTop();
											int hitWidth = asteroid.getImage().getOffsetWidth();
											int hitHeight = asteroid.getImage().getOffsetHeight();

											asteroid.setHits(1);
											asteroid.setHits(1);
											if (asteroid.getHits() >= asteroid.getMass()) {
												asteroid.getImage().removeFromParent();
												asteroids.remove(i);
												removeSpaceAsteroidRef(asteroid);
												asteroidsDestroyed++;
												updateSpaceHud();
												spawnExplosion(hitLeft, hitTop, hitWidth, hitHeight);

												if (asteroidsDestroyed > 0 && asteroidsDestroyed % 50 == 0) {
													extraLives++;
													addLifeCoinToHud();
													spawnExtraLifeBanner();
												}
											}
											cancel();
											destroy();
											return;
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
				spaceHandlersInitialized = true;
			}

			controlPanel.setCellHorizontalAlignment(hp, HasHorizontalAlignment.ALIGN_CENTER);
			controlPanel.setCellHorizontalAlignment(sf.getUpButton(), HasHorizontalAlignment.ALIGN_CENTER);
			ammoLabel.getElement().getStyle().setProperty("textAlign", "center");
			ammoLabel.getElement().getStyle().setProperty("fontSize", "20px");
			ammoLabel.getElement().getStyle().setProperty("fontWeight", "bold");
			ammoLabel.getElement().getStyle().setProperty("marginTop", "10px");
			ammoLabel.getElement().getStyle().setProperty("fontFamily", "sans-serif");
			ammoLabel.getElement().getStyle().setProperty("color", "#FFFFFF");

			survivalLabel.getElement().getStyle().setProperty("textAlign", "center");
			survivalLabel.getElement().getStyle().setProperty("fontSize", "20px");
			survivalLabel.getElement().getStyle().setProperty("fontWeight", "bold");
			survivalLabel.getElement().getStyle().setProperty("marginTop", "6px");
			survivalLabel.getElement().getStyle().setProperty("fontFamily", "sans-serif");
			survivalLabel.getElement().getStyle().setProperty("color", "#FFFFFF");

			Label legendLabel = new Label("Shoot: Space bar  •  Power-up: Every 30s");
			legendLabel.getElement().getStyle().setProperty("textAlign", "center");
			legendLabel.getElement().getStyle().setProperty("fontSize", "13px");
			legendLabel.getElement().getStyle().setProperty("fontWeight", "normal");
			legendLabel.getElement().getStyle().setProperty("marginTop", "10px");
			legendLabel.getElement().getStyle().setProperty("fontFamily", "sans-serif");
			legendLabel.getElement().getStyle().setProperty("color", "#FFFFFF");
			legendLabel.getElement().getStyle().setProperty("opacity", "0.9");

			asteroidsLabel.getElement().getStyle().setProperty("textAlign", "center");
			asteroidsLabel.getElement().getStyle().setProperty("fontSize", "20px");
			asteroidsLabel.getElement().getStyle().setProperty("fontWeight", "bold");
			asteroidsLabel.getElement().getStyle().setProperty("marginTop", "6px");
			asteroidsLabel.getElement().getStyle().setProperty("fontFamily", "sans-serif");
			asteroidsLabel.getElement().getStyle().setProperty("color", "#FFFFFF");

			lifePanel.getElement().getStyle().setProperty("marginTop", "8px");

			controlPanel.add(ammoLabel);
			controlPanel.add(survivalLabel);
			controlPanel.add(asteroidsLabel);
			controlPanel.add(legendLabel);
			controlPanel.add(lifePanel);
			controlPanel.setCellHorizontalAlignment(lifePanel, HasHorizontalAlignment.ALIGN_CENTER);

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

}
