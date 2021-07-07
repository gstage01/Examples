
import React, { Component } from 'react';

import {
  StatusBar,
  StyleSheet,
  Text,
  View,
  TouchableOpacity,
} from 'react-native';

class NavigationButton extends Component {

  render() {
    const { navigation } = this.props;
    return (
      <View>
        <TouchableOpacity
          style={styles.button}
          onPress={() => navigation.navigate("Selection", {mode: this.props.mode})}
        >
          <Text>{this.props.value}</Text>
        </TouchableOpacity>
      </View>
    )
  }
}

const styles = StyleSheet.create({
  button: {
    width: 250,
    height: 50,
    backgroundColor: '#8BF9EF',
    justifyContent:'center',
    alignItems: 'center',
    marginTop: 20,
  },
})

export default NavigationButton;
