class InstallationsController < ApplicationController
  # GET /installations
  # GET /installations.json
  def index
    @installations = Installation.all

    respond_to do |format|
      format.html # index.html.erb
      format.json { render :json => @installations }
    end
  end

  # GET /installations/1
  # GET /installations/1.json
  def show
    @installation = Installation.find(params[:id])

    respond_to do |format|
      format.html # show.html.erb
      format.json { render :json => @installation }
    end
  end

  # GET /installations/new
  # GET /installations/new.json
  def new
    @installation = Installation.new

    respond_to do |format|
      format.html # new.html.erb
      format.json { render :json => @installation }
    end
  end

  # GET /installations/1/edit
  def edit
    @installation = Installation.find(params[:id])
  end

  # POST /installations
  # POST /installations.json
  def create
    @installation = Installation.new(params[:installation])

    respond_to do |format|
      if @installation.save
        format.html { redirect_to @installation, :notice => 'Installation was successfully created.' }
        format.json { render :json => @installation, :status => :created, :location => @installation }
      else
        format.html { render :action => "new" }
        format.json { render :json => @installation.errors, :status => :unprocessable_entity }
      end
    end
  end

  # PUT /installations/1
  # PUT /installations/1.json
  def update
    @installation = Installation.find(params[:id])

    respond_to do |format|
      if @installation.update_attributes(params[:installation])
        format.html { redirect_to @installation, :notice => 'Installation was successfully updated.' }
        format.json { head :no_content }
      else
        format.html { render :action => "edit" }
        format.json { render :json => @installation.errors, :status => :unprocessable_entity }
      end
    end
  end

  # DELETE /installations/1
  # DELETE /installations/1.json
  def destroy
    @installation = Installation.find(params[:id])
    @installation.destroy

    respond_to do |format|
      format.html { redirect_to installations_url }
      format.json { head :no_content }
    end
  end
end
