/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow strict-local
 */

import React, { Component } from 'react';
import HomeScreen from "./comps/home_screen.js";
import SelectionScreen from "./comps/selection_screen.js";
import NavigationButton from "./comps/navigation_button.js";
import { createAppContainer } from 'react-navigation';
import { createStackNavigator } from 'react-navigation-stack';

import {
  StyleSheet,
  Text,
  View,
  TouchableOpacity,
} from 'react-native';


const appNavigator = createStackNavigator({
  Home: {
    screen: HomeScreen
  },
  Selection: {
    screen: SelectionScreen
  },

}, {
  initialRouteName: "Home"
});

const AppContainer = createAppContainer(appNavigator);

class App extends Component {
  render() {
    return (
      <AppContainer />
    )
  };
};

const styles = StyleSheet.create({

})

export default App;
