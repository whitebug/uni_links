#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html
#
Pod::Spec.new do |s|
  s.name             = 'uni_links'
  s.version          = '0.5.2'
  s.summary          = 'Flutter plugin for accepting incoming links - App/Deep Links (Android), Universal Links and Custom URL schemes (iOS).'
  s.description      = <<-DESC
Flutter plugin for accepting incoming links - App/Deep Links (Android), Universal Links and Custom URL schemes (iOS).
                       DESC
  s.homepage         = 'https://github.com/avioli/uni_links'
  s.license          = { :file => '../LICENSE' }
  s.author           = { 'avioli' => 'avioli@github.com' }
  s.source           = { :git => 'https://github.com/avioli/uni_links.git', :tag => s.version.to_s }
  s.source_files = 'Classes/**/*'
  s.public_header_files = 'Classes/**/*.h'
  s.dependency 'Flutter'
  
  s.ios.deployment_target = '12.0'
  s.swift_version = '5.0'
end

