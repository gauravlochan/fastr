require 'test_helper'

class InstallationsControllerTest < ActionController::TestCase
  setup do
    @installation = installations(:one)
  end

  test "should get index" do
    get :index
    assert_response :success
    assert_not_nil assigns(:installations)
  end

  test "should get new" do
    get :new
    assert_response :success
  end

  test "should create installation" do
    assert_difference('Installation.count') do
      post :create, :installation => @installation.attributes
    end

    assert_redirected_to installation_path(assigns(:installation))
  end

  test "should show installation" do
    get :show, :id => @installation
    assert_response :success
  end

  test "should get edit" do
    get :edit, :id => @installation
    assert_response :success
  end

  test "should update installation" do
    put :update, :id => @installation, :installation => @installation.attributes
    assert_redirected_to installation_path(assigns(:installation))
  end

  test "should destroy installation" do
    assert_difference('Installation.count', -1) do
      delete :destroy, :id => @installation
    end

    assert_redirected_to installations_path
  end
end
