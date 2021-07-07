import React, { Component } from 'react';

import {
  View,
  Text
} from 'react-native';

class ColorBar extends Component {

  render() {
    return (
      <View
        style={{
          backgroundColor: this.props.color,
          height: 50,
          width: 200,
          alignItems: 'center',
          justifyContent: 'center',
          borderRadius: 10,
        }
      }>
        <Text style={{color: "#FFFFFF"}}>{this.props.value}</Text>
      </View>
    )
  }
}

export default ColorBar;
