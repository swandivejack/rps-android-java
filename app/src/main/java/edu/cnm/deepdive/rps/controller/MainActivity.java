/*
 *  Copyright 2019 Nicholas Bennett & Deep Dive Coding/CNM Ingenuity
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package edu.cnm.deepdive.rps.controller;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import edu.cnm.deepdive.rps.R;
import edu.cnm.deepdive.rps.databinding.ActivityMainBinding;
import edu.cnm.deepdive.rps.viewmodel.MainViewModel;

/**
 * Controller class supporting user interaction with an instance of {@link
 * edu.cnm.deepdive.rps.model.Arena}. Note that in this implementation, this class does not itself
 * interact directly with {@link edu.cnm.deepdive.rps.model.Arena}, nor {@link
 * edu.cnm.deepdive.rps.view.TerrainView} (which displays the arena terrain). Instead, this class
 * interacts with {@link MainViewModel} to control simulation execution, and display updates are
 * managed through LiveData data binding between {@link MainViewModel} and {@link
 * edu.cnm.deepdive.rps.view.TerrainView}.
 *
 * @author Nicholas Bennett
 */
public class MainActivity extends AppCompatActivity {

  private MainViewModel viewModel;
  private boolean running;

  /**
   * Sets up a connection to an instance of {@link MainViewModel}, in order to observe and modify
   * the execution state of the simulation, and to enable LiveData data binding between {@link
   * MainViewModel} and the views in the layout (in particular, {@link
   * edu.cnm.deepdive.rps.view.TerrainView}).
   *
   * @param savedInstanceState state saved on a previous invocation of {@link
   * #onSaveInstanceState(Bundle, PersistableBundle)}.
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
    binding.setLifecycleOwner(this);
    viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
    binding.setViewModel(viewModel);
    viewModel.isRunning().observe(this, (running) -> {
      if (running != this.running) {
        this.running = running;
        invalidateOptionsMenu();
      }
    });
  }

  /**
   * Inflates the options menu containing simulation execution controls.
   *
   * @param menu {@link Menu} to which inflated items are added.
   * @return flag indicating that items have been added to {@code menu}.
   */
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    getMenuInflater().inflate(R.menu.actions, menu);
    return true;
  }

  /**
   * Updates the options menu, based on the current execution state of the {@link
   * edu.cnm.deepdive.rps.model.Arena} simulation, as reflected in {@link
   * MainViewModel#isRunning()}.
   *
   * @param menu {@link Menu} containing options menu items.
   * @return flag indicating that the menu has been modified (always {@code true}, in this case).
   */
  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    menu.findItem(R.id.run).setVisible(!running);
    menu.findItem(R.id.pause).setVisible(running);
    menu.findItem(R.id.reset).setEnabled(!running);
    return true;
  }

  /**
   * Handles user selection of an options menu item by invoking {@link MainViewModel} methods to
   * start (resume), stop, or reset the {@link edu.cnm.deepdive.rps.model.Arena} simulation.
   *
   * @param item selected item in options menu.
   * @return flag indicating whether user selection was handled by this method (or the superclass's
   * implementation).
   */
  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    boolean handled = true;
    switch (item.getItemId()) {
      case R.id.run:
        viewModel.start();
        break;
      case R.id.pause:
        viewModel.stop();
        break;
      case R.id.reset:
        viewModel.reset();
        break;
      default:
        handled = super.onOptionsItemSelected(item);
        break;
    }
    return handled;
  }

}
